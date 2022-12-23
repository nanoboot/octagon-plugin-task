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
import org.nanoboot.octagon.entity.core.ActionType;
import org.nanoboot.octagon.entity.core.Entity;
import org.nanoboot.octagon.entity.core.EntityAttribute;
import org.nanoboot.octagon.entity.core.EntityAttributeType;
import org.nanoboot.powerframework.time.moment.LocalDate;
import org.nanoboot.powerframework.time.moment.UniversalDateTime;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:robertvokac@nanoboot.org">Robert Vokac</a>
 * @since 0.1.0
 */
@Data
public abstract class AbstractTask implements Entity {

    /**
     * UUID identification of this entity.
     */
    private UUID id;
    private String name;
    private String group;
    private LocalDate since;
    private LocalDate deadline;
    private TaskStatus status;
    private TaskResolution resolution;
    private String resolutionComment;
    private Integer sortkey;
    private Importance importance;
    private Size size;
    private String note;
    private String object;
    private ReminderType reminderType;
    private String todo;
    private Boolean asap;
    private Integer repeatEveryXDays;
    //From 0 to 100 %.
    private Integer progressEstimation;
    private UUID parentId;
    private UUID assignee;
    private UUID reporter;

    @Override
    public void loadFromMap(Map<String, String> map) {
        setName(getStringParam("name", map));
        setGroup(getStringParam("group", map));
        setSince(getLocalDateParam("since", map));
        setDeadline(getLocalDateParam("deadline", map));

        String statusParam = getStringParam("status", map);
        setStatus(statusParam == null ? null : TaskStatus.valueOf(statusParam));
        String resolutionParam = getStringParam("resolution", map);
        setResolution(resolutionParam == null ? null : TaskResolution.valueOf(resolutionParam));

        setResolutionComment(getStringParam("resolutionComment", map));

        setSortkey(getIntegerParam("sortkey", map));

        String importanceParam = getStringParam("importance", map);
        setImportance(importanceParam == null ? null : Importance.valueOf(importanceParam));
        String sizeParam = getStringParam("size", map);
        setSize(sizeParam == null ? null : Size.valueOf(sizeParam));

        setNote(getStringParam("note", map));
        setObject(getStringParam("object", map));

        String reminderTypeParam = getStringParam("reminderType", map);
        setReminderType(reminderTypeParam == null ? null : ReminderType.valueOf(reminderTypeParam));

        setTodo(getStringParam("todo", map));

        setAsap(getBooleanParam("asap", map));
        setRepeatEveryXDays(getIntegerParam("repeatEveryXDays", map));

        setProgressEstimation(getIntegerParam("progressEstimation", map));

        String parentIdParam = getStringParam("parentId", map);
        setParentId(parentIdParam == null ? null : UUID.fromString(parentIdParam));
        setAssignee(getUuidParam("assignee", map));
        setReporter(getUuidParam("reporter", map));
    }

    @Override
    public String[] toStringArray() {
        return new String[]{
            id == null ? "" : id.toString(),
            name == null ? "" : name,
            group == null ? "" : group,
            since == null ? "" : since.toString(),
            deadline == null ? "" : deadline.toString(),
            status == null ? "" : status.name(),
            resolution == null ? "" : resolution.name(),
            resolutionComment == null ? "" : resolutionComment,
            sortkey == null ? "" : String.valueOf(sortkey),
            importance == null ? "" : importance.name(),
            size == null ? "" : size.name(),
            note == null ? "" : note,
            object == null ? "" : object,
            reminderType == null ? "" : reminderType.name(),
            todo == null ? "" : todo,
            asap == null ? "" : convertBooleanToString(asap),
            repeatEveryXDays == null ? "" : repeatEveryXDays.toString(),
            progressEstimation == null ? "" : String.valueOf(progressEstimation),
            parentId == null ? "" : parentId.toString(),
            assignee == null ? "" : assignee.toString(),
            reporter == null ? "" : reporter.toString(),
        };
    }

    protected List<EntityAttribute> createAbstractSchema() {
        List<EntityAttribute> ABSTRACT_SCHEMA = new ArrayList<>();

        ABSTRACT_SCHEMA.add(EntityAttribute.getIdEntityAttribute());
        ABSTRACT_SCHEMA.add(new EntityAttribute("name").withMandatory(true));
        ABSTRACT_SCHEMA.add(new EntityAttribute("group"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("since", EntityAttributeType.LOCAL_DATE).withHelp("Date, since this task should be in progress."));
        ABSTRACT_SCHEMA.add(new EntityAttribute("deadline", EntityAttributeType.LOCAL_DATE).withHelp("Date, until this task should be closed."));
        ABSTRACT_SCHEMA.add(new EntityAttribute("status",
                Arrays.stream(TaskStatus.values()).map(TaskStatus::name).collect(Collectors.toList()))
                .withDefaultValue(TaskStatus.NEW.name()).withMandatory(true).withHelp(TaskStatus.HELP));
        ABSTRACT_SCHEMA.add(new EntityAttribute("resolution",
                Arrays.stream(TaskResolution.values()).map(TaskResolution::name).collect(Collectors.toList())));
        ABSTRACT_SCHEMA.add(new EntityAttribute("resolutionComment", EntityAttributeType.TEXT_AREA));
        ABSTRACT_SCHEMA.add(new EntityAttribute("sortkey", EntityAttributeType.INTEGER).withHelp("Order number"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("importance",
                Arrays.stream(Importance.values()).map(Importance::name).collect(Collectors.toList())));
        ABSTRACT_SCHEMA.add(new EntityAttribute("size",
                Arrays.stream(Size.values()).map(Size::name).collect(Collectors.toList())));
        ABSTRACT_SCHEMA.add(new EntityAttribute("note"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("object"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("reminderType",
                Arrays.stream(ReminderType.values()).map(ReminderType::name).collect(Collectors.toList())).withDeprecated(true).withHelp("!!! Deprecated !!!"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("todo").withHelp("Todos, which must be closed before this task is closed."));
        ABSTRACT_SCHEMA.add(new EntityAttribute("asap", EntityAttributeType.BOOLEAN).withHelp("This task needs to be done as soon as possible"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("repeatEveryXDays", EntityAttributeType.INTEGER));
        ABSTRACT_SCHEMA.add(new EntityAttribute("progressEstimation", EntityAttributeType.INTEGER));
        ABSTRACT_SCHEMA.add(new EntityAttribute("parentId", "?", null, "?")
                .withCustomHumanName("Parent task"));
        //
        ABSTRACT_SCHEMA.add(new EntityAttribute("assignee", "user", "getUsers"));
        ABSTRACT_SCHEMA.add(new EntityAttribute("reporter", "user", "getUsers"));
        return ABSTRACT_SCHEMA;
    }

    @Override
    public void validate() {
        if (this.status != null && this.status.isDeprecatedStatus()) {
            throw new OctagonException("The current status " + this.status + " is deprecated. You must select another one.");
        }
        if (this.status == null || !this.status.isClosed()) {
            if (this.resolution != null) {
                throw new OctagonException("The current status " + this.status + " is not a closed status. Resolution cannot be set.");
            }
            if (this.resolutionComment != null) {
                throw new OctagonException("The current status " + this.status + " is not a closed status. Resolution comment cannot be set.");
            }
        }
        if (this.status != null && this.status.isClosed()) {
            validateClosing();
        }
    }

    private void validateClosing() {
        if (this.resolution == null) {
            throw new OctagonException("The current status " + this.status + " is a closed status, but resolution is not given.");
        }

        if (this.resolutionComment == null || this.resolutionComment.isBlank()) {
            throw new OctagonException("The current status " + this.status + " is a closed status, but resolutionComment is empty. Please, fill the resolutionComment property.");
        }

        if (this.todo != null) {
            throw new OctagonException("The current status " + this.status + " is a closed status, but todo is not empty. Todos must be done, before closing. Fix the todos and move the notes to note property.");
        }
    }

    @Override
    public void validateCreate() {
        validateDeadline(null, this.deadline);
        if (this.status != null) {
            if (!TaskStatus.canTransitTo(null, this.status)) {
                throw new OctagonException("The start status cannot be " + this.status + ". You must select another one.");
            }
        }

    }

    @Override
    public void validateDelete() {
        if (this.status == null || !this.status.isClosed()) {
            throw new OctagonException("Task cannot be deleted, because it is not yet closed.");
        }
    }

    @Override
    public void validateQuestionAnswers(Map<String, String> answers, ActionType actionType) {
        if (actionType == ActionType.UPDATE && this.getStatus().isClosed() && answers.get("hasNotYetClosedChildren").equals("true")) {
            throw new OctagonException("Cannot close task, because not all its children are closed " + toString());
        }
        if (parentId != null && (actionType == ActionType.CREATE || actionType == ActionType.UPDATE) && answers.get("isParentClosed").equals("true")) {
            throw new OctagonException("Cannot create new task or update existing task, if parent is closed. " + toString());
        }
        if (actionType == ActionType.DELETE && answers.get("hasChildren").equals("true")) {
            throw new OctagonException("Cannot delete task " + toString() + ", because it has some children");
        }
    }

    @Override
    public void validateUpdate(Entity updated) {
        AbstractTask updatedTask = (AbstractTask) updated;
        if (updatedTask.getResolution() == null) {
            validateDeadline(this.deadline, updatedTask.deadline);
        }

        TaskStatus newStatus = updatedTask.getStatus();
        TaskStatus oldStatus = this.status;
        if (oldStatus != null && newStatus == null) {
            throw new OctagonException("Status cannot be changed to null.");
        }
        if (oldStatus == null && newStatus != null) {

            if (!TaskStatus.canTransitTo(null, newStatus)) {
                throw new OctagonException("The start status cannot be " + newStatus + ". You must select another one.");
            }
        }
        if (oldStatus != null && newStatus != null) {
            if (!TaskStatus.canTransitTo(oldStatus, newStatus)) {
                throw new OctagonException("The status " + oldStatus + " cannot be changed to " + newStatus + ". You must select another one.");
            }
        }
        if (oldStatus != null && oldStatus.isClosed() && newStatus != null && newStatus.isClosed()) {
            if (oldStatus == newStatus) {
                throw new OctagonException("Cannot change task, if before and after status is a closed status and these statuses are equal.");
            }
        }
        if (newStatus != null && newStatus != TaskStatus.RESOLVED && newStatus.isClosed()) {
            if (this.resolution != updatedTask.getResolution()) {
                throw new OctagonException("Resolution cannot be changed, if new task status is not RESOLVED");
            }
        }
    }

    private static void validateDeadline(LocalDate oldDeadlineLocalDate, LocalDate newDeadlineLocalDate) {

        Integer oldDeadline = oldDeadlineLocalDate == null ? null : Integer.valueOf(oldDeadlineLocalDate.toString().replaceAll("-", ""));
        Integer newDeadline = newDeadlineLocalDate == null ? null : Integer.valueOf(newDeadlineLocalDate.toString().replaceAll("-", ""));
        LocalDate nowLocalDate = UniversalDateTime.now().getDate();
        Integer now = Integer.valueOf(nowLocalDate.toString().replaceAll("-", ""));
        if (newDeadline != null && (oldDeadline == null || (oldDeadline.intValue() != newDeadline.intValue()))) {
            if (newDeadline < now) {
                throw new OctagonException("Cannot change deadline to date in past: " + newDeadline);
            }
        }
    }

    @Override
    public List<String> listQuestionsToAsk() {
        return Stream.of("hasChildren", "hasNotYetClosedChildren", "isParentClosed(parentId)").collect(Collectors.toList());
    }
//
//    public String getDefaultOrder() {
//        return "sortkey asc";
//    }
}
