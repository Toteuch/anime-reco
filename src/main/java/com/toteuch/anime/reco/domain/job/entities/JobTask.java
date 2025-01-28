package com.toteuch.anime.reco.domain.job.entities;

import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobStatus;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class JobTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @Enumerated(EnumType.STRING)
    private JobName name;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    @Column(columnDefinition = "DATETIME (3)", nullable = false)
    private Date createdAt;
    @Column(columnDefinition = "DATETIME (3)")
    private Date startedAt;
    @Column(columnDefinition = "DATETIME (3)", nullable = false)
    private Date updatedAt;
    @Column(columnDefinition = "DATETIME (3)")
    private Date endedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Author author;
    private Long readItemCount;
    private Long processItemCount;

    public JobTask(Profile profile, JobName name, Author author) {
        this.profile = profile;
        this.name = name;
        this.author = author;
        this.status = JobStatus.QUEUED;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public JobTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public JobName getName() {
        return name;
    }

    public void setName(JobName name) {
        this.name = name;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getReadItemCount() {
        return readItemCount;
    }

    public void setReadItemCount(Long readItemCount) {
        this.readItemCount = readItemCount;
    }

    public Long getProcessItemCount() {
        return processItemCount;
    }

    public void setProcessItemCount(Long processItemCount) {
        this.processItemCount = processItemCount;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
