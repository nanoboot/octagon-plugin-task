///////////////////////////////////////////////////////////////////////////////////////////////
// Octagon Plugin Task: Task plugin for Octagon application.
// Copyright (C) 2021-2022 the original author or authors.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; version 2
// of the License only.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
///////////////////////////////////////////////////////////////////////////////////////////////

package org.nanoboot.octagon.plugin.task;

import org.nanoboot.octagon.core.exceptions.OctagonException;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:robertvokac@nanoboot.org">Robert Vokac</a>
 * @since 0.1.0
 */
public enum TaskStatus {
    @Deprecated
    CONFIRMED(true),
    @Deprecated
    OPEN(true),
    @Deprecated
    LONG_RUNNING(true),
    UNCONFIRMED,
    NEW,
    ASSIGNED,
    IN_PROGRESS,
    PAUSED,
    STALLED,
    BLOCKED,
    REOPENED,
    RESOLVED,
    VERIFIED,
    CLOSED;

    public static final TaskStatus DEFAULT_TASK_STATUS = NEW;

    private static final List<TaskStatus> START_STATUSES_ALLOWED_TRANSITIONS = Stream.of(UNCONFIRMED, NEW, ASSIGNED).collect(Collectors.toList());

    private static final List<TaskStatus> CONFIRMED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(NEW, ASSIGNED, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> OPEN_STATUSES_ALLOWED_TRANSITIONS = Stream.of(IN_PROGRESS).collect(Collectors.toList());
    private static final List<TaskStatus> LONG_RUNNING_STATUSES_ALLOWED_TRANSITIONS = Stream.of(IN_PROGRESS, PAUSED, STALLED, BLOCKED, RESOLVED).collect(Collectors.toList());

    private static final List<TaskStatus> UNCONFIRMED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(NEW, ASSIGNED, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> NEW_STATUSES_ALLOWED_TRANSITIONS = Stream.of(ASSIGNED, IN_PROGRESS, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> ASSIGNED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(NEW, RESOLVED, IN_PROGRESS).collect(Collectors.toList());
    private static final List<TaskStatus> IN_PROGRESS_STATUSES_ALLOWED_TRANSITIONS = Stream.of(UNCONFIRMED, PAUSED, STALLED, BLOCKED, RESOLVED).collect(Collectors.toList());

    private static final List<TaskStatus> PAUSED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(STALLED, BLOCKED, REOPENED, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> STALLED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(BLOCKED, REOPENED, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> BLOCKED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(STALLED, REOPENED, RESOLVED).collect(Collectors.toList());
    private static final List<TaskStatus> REOPENED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(NEW, ASSIGNED, RESOLVED).collect(Collectors.toList());

    private static final List<TaskStatus> RESOLVED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(UNCONFIRMED, REOPENED, VERIFIED).collect(Collectors.toList());
    private static final List<TaskStatus> VERIFIED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(UNCONFIRMED, REOPENED, RESOLVED, CLOSED).collect(Collectors.toList());
    private static final List<TaskStatus> CLOSED_STATUSES_ALLOWED_TRANSITIONS = Stream.of(UNCONFIRMED, REOPENED, RESOLVED).collect(Collectors.toList());

    private static final Map<TaskStatus, List<TaskStatus>> allowedStatusTransitions;

    static {
        allowedStatusTransitions = new HashMap<>();

        allowedStatusTransitions.put(CONFIRMED, CONFIRMED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(OPEN, OPEN_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(LONG_RUNNING, LONG_RUNNING_STATUSES_ALLOWED_TRANSITIONS);
        //
        allowedStatusTransitions.put(UNCONFIRMED, UNCONFIRMED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(NEW, NEW_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(ASSIGNED, ASSIGNED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(IN_PROGRESS, IN_PROGRESS_STATUSES_ALLOWED_TRANSITIONS);

        allowedStatusTransitions.put(PAUSED, PAUSED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(STALLED, STALLED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(BLOCKED, BLOCKED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(REOPENED, REOPENED_STATUSES_ALLOWED_TRANSITIONS);

        allowedStatusTransitions.put(RESOLVED, RESOLVED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(VERIFIED, VERIFIED_STATUSES_ALLOWED_TRANSITIONS);
        allowedStatusTransitions.put(CLOSED, CLOSED_STATUSES_ALLOWED_TRANSITIONS);
    }

    public static final String HELP;

    static {
        StringBuilder sb = new StringBuilder();
        for (TaskStatus ts : TaskStatus.values()) {
            List<TaskStatus> list = allowedStatusTransitions.get(ts);
            int index = 0;
            int lastIndex = list.size() - 1;

            if (ts.isDeprecatedStatus()) {
                sb.append("--");
            }
            sb.append(ts.name());

            if (ts.isDeprecatedStatus()) {
                sb.append("--");
            }
            sb.append(" >> ");
            for (TaskStatus t : list) {
                sb.append(t.name());
                if (index < lastIndex) {
                    sb.append(" / ");
                }
                ++index;
            }
            sb.append("\n");
        }
        HELP = sb.toString();
    }

    @Getter
    private final boolean deprecatedStatus;

    TaskStatus() {
        this(false);
    }

    TaskStatus(boolean b) {
        this.deprecatedStatus = b;
    }

    public static boolean canTransitTo(TaskStatus fromStatus, TaskStatus toStatus) {
        if (toStatus == null) {
            throw new OctagonException("Cannot change status to null: toStatus is null.");
        }
        if (toStatus.isDeprecatedStatus()) {
            throw new OctagonException("Cannot change to deprecated status: " + toStatus);
        }

        if (fromStatus == toStatus) {
            return true;
        }
        if (fromStatus == null) {
            return containsListEnum(START_STATUSES_ALLOWED_TRANSITIONS, toStatus);
        }

        List<TaskStatus> allowedStatuses = allowedStatusTransitions.get(fromStatus);

        return allowedStatuses.contains(toStatus);
    }

    public List<TaskStatus> getAllowedStatusTransitions() {
        List<TaskStatus> allowedStatuses = allowedStatusTransitions.get(this);
        return allowedStatuses;
    }

    private static boolean containsListEnum(List<TaskStatus> list, TaskStatus ts) {
        System.err.println("Searching " + ts.name());
        for (TaskStatus t : list) {
            System.out.println("Comparing " + ts + " to " + t);
            if (ts.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean isClosed() {
        return this == RESOLVED || this == VERIFIED || this == CLOSED;
    }
}
