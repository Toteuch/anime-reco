package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobStatus;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobTaskRepository extends JpaRepository<JobTask, Long> {

    List<JobTask> findByProfileSubAndName(String sub, JobName name, Sort sort);

    List<JobTask> findByProfileSubAndNameAndStatus(String sub, JobName name, JobStatus status);

    List<JobTask> findByProfileSubAndNameAndStatus(String sub, JobName name, JobStatus status, Sort sort);

    JobTask findByStatus(JobStatus status, Sort sort, Limit limit);

    JobTask findByProfileSubAndId(String sub, Long jobTaskId);
}
