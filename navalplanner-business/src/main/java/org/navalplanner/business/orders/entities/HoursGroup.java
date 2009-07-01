package org.navalplanner.business.orders.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.NotNull;
import org.navalplanner.business.resources.entities.Criterion;
import org.navalplanner.business.resources.entities.ICriterionType;

public class HoursGroup implements Cloneable {

    private Long id;

    private Long version;

    public Long getId() {
        return id;
    }

    @NotNull
    private Integer workingHours = 0;

    private BigDecimal percentage = new BigDecimal(0).setScale(2);

    private Boolean fixedPercentage = false;

    private Set<Criterion> criterions = new HashSet<Criterion>();

    @NotNull
    private OrderLine parentOrderLine;

    /**
     * Constructor for hibernate. Do not use!
     */
    public HoursGroup() {
    }

    public HoursGroup(OrderLine parentOrderLine) {
        this.parentOrderLine = parentOrderLine;
    }

    public void setWorkingHours(Integer workingHours)
            throws IllegalArgumentException {
        if (workingHours < 0) {
            throw new IllegalArgumentException(
                    "Working hours shouldn't be neagtive");
        }

        this.workingHours = workingHours;
    }

    public Integer getWorkingHours() {
        return workingHours;
    }

    public void setPercentage(BigDecimal percentage)
            throws IllegalArgumentException {
        BigDecimal oldPercentage = this.percentage;

        this.percentage = percentage;

        if (!parentOrderLine.isPercentageValid()) {
            this.percentage = oldPercentage;
            throw new IllegalArgumentException(
                    "Total percentage should be less than 100%");
        }
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setFixedPercentage(Boolean fixedPercentage) {
        this.fixedPercentage = fixedPercentage;
    }

    public Boolean isFixedPercentage() {
        return this.fixedPercentage;
    }

    public void setCriterions(Set<Criterion> criterions) {
        this.criterions = criterions;
    }

    public Set<Criterion> getCriterions() {
        return criterions;
    }

    public void addCriterion(Criterion criterion) {
        Criterion oldCriterion = getCriterionByType(criterion.getType());
        if (oldCriterion != null) {
            removeCriterion(oldCriterion);
        }

        criterions.add(criterion);
    }

    public void removeCriterion(Criterion criterion) {
        criterions.remove(criterion);
    }

    public Criterion getCriterionByType(ICriterionType<?> type) {
        for (Criterion criterion : criterions) {
            if (criterion.getType().equals(type.getName())) {
                return criterion;
            }
        }

        return null;
    }

    public Criterion getCriterionByType(String type) {
        for (Criterion criterion : criterions) {
            if (criterion.getType().equals(type)) {
                return criterion;
            }
        }

        return null;
    }

    public void removeCriterionByType(ICriterionType<?> type) {
        Criterion criterion = getCriterionByType(type);
        if (criterion != null) {
            removeCriterion(criterion);
        }
    }

    public void removeCriterionByType(String type) {
        Criterion criterion = getCriterionByType(type);
        if (criterion != null) {
            removeCriterion(criterion);
        }
    }

    public void forceLoadCriterions() {
        criterions.size();
    }

    public void setParentOrderLine(OrderLine parentOrderLine) {
        this.parentOrderLine = parentOrderLine;
    }

    public OrderLine getParentOrderLine() {
        return parentOrderLine;
    }

    public void makeTransientAgain() {
        // FIXME Review reattachment
        id = null;
        version = null;
    }

    public boolean isTransient() {
     // FIXME Review reattachment
        return id == null;
    }

}
