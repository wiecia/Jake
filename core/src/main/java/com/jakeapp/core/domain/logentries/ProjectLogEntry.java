package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.UUID;
import java.io.Serializable;


/**
 * This is the abstract superclass for all <code>Project</code> specific <code>LogEntry</code>s.
 * Only <code>ProjectLogEntry</code> <code>LogEntry</code> are able to have a <code>Project</code> in the
 * <code>belongsTo</code> member.
 */
@Entity
public abstract class ProjectLogEntry extends LogEntry<Project> implements Serializable {
    private static final long serialVersionUID = -8773156028147182736L;

    @Transient
    private Project project;

    public ProjectLogEntry(LogAction logAction, Project project, User member) {
        super(UUID.randomUUID(),
				logAction, getTime(), project, member, null,
				null, true);
        this.project = project;
    }

    public void setProject(Project p)
    {
        this.project = p;
    }

    public Project getProject()
    {
        return project;
    }

    public ProjectLogEntry() {
    }


    public String toString()
    {
        return "Project: " + this.project + " " + super.toString();
    }


    

}
