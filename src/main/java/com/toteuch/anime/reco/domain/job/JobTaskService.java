package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.data.repository.JobTaskRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobTaskService {
    private static final Logger log = LoggerFactory.getLogger(JobTaskService.class);

    @Autowired
    private JobTaskRepository repo;
    @Autowired
    private ProfileService profileService;

    public JobTask create(String sub, JobName name, Author author) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("createJobTask failed, Profile notFound");
        if (null == profile.getUser()) throw new AnimeRecoException("createJobTask failed, Profile isn't linked to a " +
                "user");
        JobTask jobTask = repo.findByProfileSubAndNameAndStatus(sub, name, JobStatus.QUEUED);
        if (null != jobTask) throw new AnimeRecoException("createJobTask failed, JobTask already queued for that " +
                "Profile");
        jobTask = repo.save(new JobTask(profile, name, author));
        log.debug("JobTask {} ({}) created by {} for Profile {}", name.name(), jobTask.getId(), author.name(), sub);
        return jobTask;
    }

    public void end(JobTask jobTask) {
        jobTask = repo.findById(jobTask.getId()).orElse(null);
        jobTask.setEndedAt(new Date());
        repo.save(jobTask);
    }

    public boolean isAbanonned(JobTask jobTask) {
        jobTask = repo.findById(jobTask.getId()).orElse(null);
        return jobTask.getStatus() == JobStatus.ABANDONED;
    }

    public void abandon(String sub, Long jobTaskId) throws AnimeRecoException {
        JobTask jobTask = repo.findByProfileSubAndId(sub, jobTaskId);
        if (!isAbandonnable(jobTask.getStatus())) throw new AnimeRecoException("JobTask can't be abandoned");
        jobTask.setStatus(JobStatus.ABANDONED);
        repo.save(jobTask);
        log.debug("JobTask {} ({}) has been abandoned", jobTask.getName().name(), jobTaskId);
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

    public void complete(Long jobTaskId) throws AnimeRecoException {
        JobTask jobTask = repo.findById(jobTaskId).orElse(null);
        if (jobTask == null) throw new AnimeRecoException("Completing taskJob failed, jobTask not found");
        jobTask.setStatus(JobStatus.COMPLETED);
        jobTask.setEndedAt(new Date());
        jobTask.setUpdatedAt(new Date());
        log.debug("JobTask {} ({}) completed", jobTask.getName().name(), jobTaskId);
        repo.save(jobTask);
    }

    public JobTask setReadItemCount(JobTask jobTask, long readItemCount) {
        jobTask.setReadItemCount(readItemCount);
        jobTask.setUpdatedAt(new Date());
        return repo.save(jobTask);
    }

    public void fail(JobTask jobTask) {
        jobTask.setStatus(JobStatus.FAILED);
        jobTask.setEndedAt(new Date());
        jobTask.setUpdatedAt(new Date());
        repo.save(jobTask);
        log.debug("JobTask {} ({}) failed", jobTask.getName().name(), jobTask.getId());
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

    private boolean isAbandonnable(JobStatus status) {
        return status.ordinal() < JobStatus.COMPLETED.ordinal();
    }
}
