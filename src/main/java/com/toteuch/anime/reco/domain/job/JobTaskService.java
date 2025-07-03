package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.data.repository.JobTaskRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.UserSimilarityService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class JobTaskService {
    private static final Logger log = LoggerFactory.getLogger(JobTaskService.class);

    @Autowired
    private JobTaskRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserSimilarityService userSimilarityService;

    public void requestClearOldData(Author author) {
        List<Profile> profiles = profileService.getAll();
        for (Profile profile : profiles) {
            boolean startTask = profile.getUser() != null;
            // Reject if the profile is not linked to a user
            JobTask lastClearOldDataForProfile = getLastCompletedJobTaskExecution(profile.getSub(),
                    JobName.CLEAR_OLD_DATA);
            Calendar limitDateForCOD = Calendar.getInstance();
            limitDateForCOD.add(Calendar.HOUR, -23);
            if (startTask && lastClearOldDataForProfile == null) {
                JobTask lastProcessUserSimilarity = getLastCompletedJobTaskExecution(profile.getSub(),
                        JobName.PROCESS_USER_SIMILARITY);
                if (lastProcessUserSimilarity == null) {
                    // Reject if profile never completed processUserSimilarity
                    startTask = false;
                }
            } else if (startTask && lastClearOldDataForProfile.getCreatedAt().after(limitDateForCOD.getTime())) {
                // Reject if the last run for this profile is lesser than 1 day
                startTask = false;
            }
            if (startTask) {
                Calendar creationDate = Calendar.getInstance();
                try {
                    create(profile.getSub(), JobName.CLEAR_OLD_DATA, author, creationDate.getTime());
                    creationDate.add(Calendar.SECOND, 1);
                    create(profile.getSub(), JobName.PROCESS_USER_SIMILARITY, author, creationDate.getTime());
                    creationDate.add(Calendar.SECOND, 1);
                    create(profile.getSub(), JobName.PROCESS_ANIME_RECOMMENDATION, author, creationDate.getTime());
                } catch (AnimeRecoException e) {
                    log.error("Couldn't create refresh profile data tasks", e);
                }
            }
        }
    }

    public JobTask create(String sub, JobName name, Author author) throws AnimeRecoException {
        return create(sub, name, author, new Date());
    }

    public JobTask getLastOccurrence(String sub, JobName name, JobStatus status) {
        List<JobTask> taskList = repo.findByProfileSubAndName(sub, name, Sort.by(Sort.Direction.DESC, "updatedAt"));
        if (taskList != null && !taskList.isEmpty()) {
            if (status == null) {
                return taskList.get(0);
            }
            for (JobTask task : taskList) {
                if (task.getStatus() == status) {
                    return task;
                }
            }
        }
        return null;
    }

    private JobTask create(String sub, JobName name, Author author, Date createAt) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("Profile notFound");
        switch (name) {
            case PROCESS_USER_SIMILARITY:
                if (null == profile.getUser())
                    throw new AnimeRecoException("Profile isn't linked to a user");
                break;
            case PROCESS_ANIME_RECOMMENDATION:
                JobTask lastPUS = getLastOccurrence(profile.getSub(), JobName.PROCESS_USER_SIMILARITY, JobStatus.COMPLETED);
                if (lastPUS == null)
                    throw new AnimeRecoException("Please complete the user similarities before");
                break;
            case CLEAR_OLD_DATA:
                break;
            default:
                throw new AnimeRecoException("createJobTask failed, Job not implemented yet");
        }
        List<JobTask> jobTaskList = repo.findByProfileSubAndNameAndStatus(sub, name, JobStatus.QUEUED);
        JobTask jobTask = null;
        if (null != jobTaskList) {
            if (jobTaskList.size() > 1) {
                throw new AnimeRecoException("There is more than 1 task queued for this job");
            } else if (jobTaskList.size() == 1) {
                jobTask = jobTaskList.get(0);
                if (author == Author.USER) {
                    throw new AnimeRecoException("Job already queued for that Profile");
                } else {
                    // put the job at the end of the queue
                    abandon(sub, jobTask.getId());
                }
            }
        }
        jobTask = new JobTask(profile, name, author);
        jobTask.setCreatedAt(createAt);
        jobTask = repo.save(jobTask);
        log.debug("JobTask {} ({}) created by {} for Profile {}", name.name(), jobTask.getId(), author.name(), sub);
        return jobTask;
    }

    private JobTask getLastCompletedJobTaskExecution(String sub, JobName jobName) {
        List<JobTask> jobTaskList = repo.findByProfileSubAndNameAndStatus(sub, jobName, JobStatus.COMPLETED,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        if (jobTaskList != null && !jobTaskList.isEmpty()) {
            return jobTaskList.get(0);
        }
        return null;
    }

    private boolean isAbandonnable(JobStatus status) {
        return status.ordinal() < JobStatus.COMPLETED.ordinal();
    }

    public boolean isAbandonned(JobTask jobTask) {
        jobTask = repo.findById(jobTask.getId()).orElse(null);
        return jobTask.getStatus() == JobStatus.ABANDONED;
    }

    public JobTask getNextQueued() {
        return repo.findByStatus(JobStatus.QUEUED, Sort.by(Sort.Direction.ASC, "createdAt"), Limit.of(1));
    }

    public JobTask start(Long jobTaskId) throws AnimeRecoException {
        JobTask runningTask = repo.findByStatus(JobStatus.STARTED, Sort.by("startedAt"), Limit.of(1));
        if (runningTask != null) throw new AnimeRecoException(
                "Starting taskJob %s failed, a jobTask is already running".formatted(jobTaskId));
        JobTask jobTask = repo.findById(jobTaskId).orElse(null);
        if (jobTask == null) throw new AnimeRecoException("Starting taskJob failed, jobTask not found");
        jobTask.setStartedAt(new Date());
        jobTask.setStatus(JobStatus.STARTED);
        jobTask.setUpdatedAt(new Date());
        log.debug("JobTask {} ({}) started", jobTask.getName().name(), jobTaskId);
        return repo.save(jobTask);
    }

    public void end(JobTask jobTask) {
        jobTask = repo.findById(jobTask.getId()).orElse(null);
        jobTask.setEndedAt(new Date());
        repo.save(jobTask);
    }

    public void complete(Long jobTaskId) throws AnimeRecoException {
        JobTask jobTask = repo.findById(jobTaskId).orElse(null);
        if (jobTask == null) throw new AnimeRecoException("Completing taskJob failed, jobTask not found");
        jobTask.setStatus(JobStatus.COMPLETED);
        jobTask.setEndedAt(new Date());
        jobTask.setUpdatedAt(new Date());
        log.debug("JobTask {} ({}) completed", jobTask.getName().name(), jobTaskId);
        repo.save(jobTask);
    }

    public void fail(JobTask jobTask) {
        jobTask.setStatus(JobStatus.FAILED);
        jobTask.setEndedAt(new Date());
        jobTask.setUpdatedAt(new Date());
        repo.save(jobTask);
        log.debug("JobTask {} ({}) failed", jobTask.getName().name(), jobTask.getId());
    }

    public void abandon(String sub, Long jobTaskId) throws AnimeRecoException {
        JobTask jobTask = repo.findByProfileSubAndId(sub, jobTaskId);
        if (!isAbandonnable(jobTask.getStatus())) throw new AnimeRecoException("Job can't be abandoned");
        jobTask.setStatus(JobStatus.ABANDONED);
        repo.save(jobTask);
        log.debug("JobTask {} ({}) has been abandoned", jobTask.getName().name(), jobTaskId);
    }

    public JobTask setReadItemCount(JobTask jobTask, long readItemCount) {
        jobTask.setReadItemCount(readItemCount);
        jobTask.setUpdatedAt(new Date());
        return repo.save(jobTask);
    }

    public synchronized void appendWriteItemCount(Long taskId, long itemCount) throws AnimeRecoException {
        JobTask jobTask = repo.findById(taskId).orElseThrow(() ->
                new AnimeRecoException(("appendWriteItemCount for taskId %d failed").formatted(taskId)));
        Long processItemCount = jobTask.getProcessItemCount();
        if (processItemCount == null) processItemCount = 0L;
        jobTask.setProcessItemCount(processItemCount + itemCount);
        jobTask.setUpdatedAt(new Date());
        repo.save(jobTask);
    }
}
