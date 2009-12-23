/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.navalplanner.web.test.ws.orders;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.navalplanner.business.BusinessGlobalNames.BUSINESS_SPRING_CONFIG_FILE;
import static org.navalplanner.web.WebappGlobalNames.WEBAPP_SPRING_CONFIG_FILE;
import static org.navalplanner.web.test.WebappGlobalNames.WEBAPP_SPRING_CONFIG_TEST_FILE;
import static org.navalplanner.web.WebappGlobalNames.WEBAPP_SPRING_SECURITY_CONFIG_FILE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.navalplanner.business.IDataBootstrap;
import org.navalplanner.business.orders.daos.IOrderDAO;
import org.navalplanner.business.orders.daos.IOrderElementDAO;
import org.navalplanner.ws.common.api.ConstraintViolationDTO;
import org.navalplanner.ws.common.api.InstanceConstraintViolationsDTO;
import org.navalplanner.ws.common.api.ResourceEnumDTO;
import org.navalplanner.ws.orders.api.HoursGroupDTO;
import org.navalplanner.ws.orders.api.IOrderElementService;
import org.navalplanner.ws.orders.api.LabelDTO;
import org.navalplanner.ws.orders.api.MaterialAssignmentDTO;
import org.navalplanner.ws.orders.api.OrderDTO;
import org.navalplanner.ws.orders.api.OrderLineDTO;
import org.navalplanner.ws.orders.api.OrderLineGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests for {@link IOrderElementService}.
 *
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { BUSINESS_SPRING_CONFIG_FILE,
        WEBAPP_SPRING_CONFIG_FILE, WEBAPP_SPRING_CONFIG_TEST_FILE,
        WEBAPP_SPRING_SECURITY_CONFIG_FILE})
@Transactional
public class OrderElementServiceTest {

    @Resource
    private IDataBootstrap defaultAdvanceTypesBootstrapListener;

    @Resource
    private IDataBootstrap configurationBootstrap;

    @Resource
    private IDataBootstrap materialCategoryBootstrap;

    @Before
    public void loadRequiredaData() {
        defaultAdvanceTypesBootstrapListener.loadRequiredData();
        configurationBootstrap.loadRequiredData();
        materialCategoryBootstrap.loadRequiredData();
    }

    @Autowired
    private IOrderElementService orderElementService;

    @Autowired
    private IOrderDAO orderDAO;

    @Autowired
    private IOrderElementDAO orderElementDAO;

    @Test
    public void invalidOrderWithoutAttributes() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: code, name. Check constraints:
        // checkConstraintOrderMustHaveStartDate
        assertThat(constraintViolations.size(), equalTo(3));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void invalidOrderWithoutNameAndInitDate() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.code = "order-code";

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: name. Check constraints:
        // checkConstraintOrderMustHaveStartDate
        assertThat(constraintViolations.size(), equalTo(2));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void invalidOrderWithoutCodeAndInitDate() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: code. Check constraints:
        // checkConstraintOrderMustHaveStartDate
        assertThat(constraintViolations.size(), equalTo(2));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void invalidOrderWithoutCodeAndName() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.initDate = new Date();

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: code, name
        assertThat(constraintViolations.size(), equalTo(2));
        for (ConstraintViolationDTO constraintViolationDTO : constraintViolations) {
            assertThat(constraintViolationDTO.fieldName, anyOf(
                    equalTo("Order::code"), equalTo("Order::name")));
        }

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void validOrder() {
        String code = "order-code";
        int previous = orderElementDAO.findByCode(code).size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = code;
        orderDTO.initDate = new Date();

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(0));

        assertThat(orderElementDAO.findByCode(code).size(),
                equalTo(previous + 1));
    }

    @Test
    public void orderWithInvalidOrderLine() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderDTO.children.add(orderLineDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: code, name. Check constraints:
        // checkConstraintAtLeastOneHoursGroupForEachOrderElement
        assertThat(constraintViolations.size(), equalTo(3));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void orderWithOrderLineWithoutHoursGroup() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.name = "Order line";
        orderLineDTO.code = "order-line-code";
        orderDTO.children.add(orderLineDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Check constraints:
        // checkConstraintAtLeastOneHoursGroupForEachOrderElement
        assertThat(constraintViolations.size(), equalTo(1));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void orderWithOrderLineWithInvalidHoursGroup() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.name = "Order line";
        orderLineDTO.code = "order-line-code";
        HoursGroupDTO hoursGroupDTO = new HoursGroupDTO();
        hoursGroupDTO.resourceType = ResourceEnumDTO.WORKER;
        orderLineDTO.hoursGroups.add(hoursGroupDTO);
        orderDTO.children.add(orderLineDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: name, workingHours
        assertThat(constraintViolations.size(), equalTo(2));
        for (ConstraintViolationDTO constraintViolationDTO : constraintViolations) {
            assertThat(constraintViolationDTO.fieldName, anyOf(
                    equalTo("HoursGroup::name"),
                    equalTo("HoursGroup::workingHours")));
        }

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void validOrderWithOrderLine() {
        String code = "order-code";
        int previous = orderElementDAO.findByCode(code).size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = code;
        orderDTO.initDate = new Date();

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.name = "Order line";
        orderLineDTO.code = "order-line-code";
        HoursGroupDTO hoursGroupDTO = new HoursGroupDTO("hours-group",
                ResourceEnumDTO.WORKER, 1000);
        orderLineDTO.hoursGroups.add(hoursGroupDTO);
        orderDTO.children.add(orderLineDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(0));

        assertThat(orderElementDAO.findByCode(code).size(),
                equalTo(previous + 1));
    }

    @Test
    public void orderWithInvalidOrderLineGroup() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        OrderLineGroupDTO orderLineGroupDTO = new OrderLineGroupDTO();
        orderDTO.children.add(orderLineGroupDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: code, name. Check constraints:
        // checkConstraintAtLeastOneHoursGroupForEachOrderElement
        assertThat(constraintViolations.size(), equalTo(3));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void orderWithOrderLineGroupWithoutHoursGroup() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        OrderLineGroupDTO orderLineGroupDTO = new OrderLineGroupDTO();
        orderLineGroupDTO.name = "Order line group";
        orderLineGroupDTO.code = "order-line-group-code";
        orderDTO.children.add(orderLineGroupDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Check constraints:
        // checkConstraintAtLeastOneHoursGroupForEachOrderElement
        assertThat(constraintViolations.size(), equalTo(1));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void validOrderWithOrderLineGroup() {
        String code = "order-code";
        int previous = orderElementDAO.findByCode(code).size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = code;
        orderDTO.initDate = new Date();

        OrderLineGroupDTO orderLineGroupDTO = new OrderLineGroupDTO();
        orderLineGroupDTO.name = "Order line group";
        orderLineGroupDTO.code = "order-line-group-code";

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.name = "Order line";
        orderLineDTO.code = "order-line-code";
        HoursGroupDTO hoursGroupDTO = new HoursGroupDTO("hours-group",
                ResourceEnumDTO.WORKER, 1000);
        orderLineDTO.hoursGroups.add(hoursGroupDTO);
        orderLineGroupDTO.children.add(orderLineDTO);

        orderDTO.children.add(orderLineGroupDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(0));

        assertThat(orderElementDAO.findByCode(code).size(),
                equalTo(previous + 1));
    }

    @Ignore
    @Test
    public void orderWithInvalidMaterialAssignment() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        MaterialAssignmentDTO materialAssignmentDTO = new MaterialAssignmentDTO();
        orderDTO.materialAssignments.add(materialAssignmentDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: material, units, unitPrice
        assertThat(constraintViolations.size(), equalTo(3));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void orderWithInvalidMaterialAssignmentWithoutUnitsAndUnitPrice() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        MaterialAssignmentDTO materialAssignmentDTO = new MaterialAssignmentDTO();
        materialAssignmentDTO.materialCode = "material-code";
        orderDTO.materialAssignments.add(materialAssignmentDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: units, unitPrice
        assertThat(constraintViolations.size(), equalTo(2));
        for (ConstraintViolationDTO constraintViolationDTO : constraintViolations) {
            assertThat(constraintViolationDTO.fieldName, anyOf(
                    equalTo("MaterialAssignment::units"),
                    equalTo("MaterialAssignment::unitPrice")));
        }

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void validOrderWithMaterialAssignment() {
        String code = "order-code";
        int previous = orderElementDAO.findByCode(code).size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = code;
        orderDTO.initDate = new Date();

        MaterialAssignmentDTO materialAssignmentDTO = new MaterialAssignmentDTO();
        materialAssignmentDTO.materialCode = "material-code";
        materialAssignmentDTO.unitPrice = BigDecimal.TEN;
        materialAssignmentDTO.units = 100.0;
        orderDTO.materialAssignments.add(materialAssignmentDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(0));

        assertThat(orderElementDAO.findByCode(code).size(),
                equalTo(previous + 1));
    }

    @Test
    public void orderWithInvalidLabel() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        LabelDTO labelDTO = new LabelDTO();
        orderDTO.labels.add(labelDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: material, units, unitPrice
        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.get(0).fieldName,
                equalTo("LabelType::name"));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void orderWithInvalidLabelWithoutName() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.type = "Label type";
        orderDTO.labels.add(labelDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: material, units, unitPrice
        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.get(0).fieldName,
                equalTo("Label::name"));

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

    @Test
    public void validOrderWithLabel() {
        String code = "order-code";
        int previous = orderElementDAO.findByCode(code).size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = code;
        orderDTO.initDate = new Date();

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.name = "Label name";
        labelDTO.type = "Label type";
        orderDTO.labels.add(labelDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(0));

        assertThat(orderElementDAO.findByCode(code).size(),
                equalTo(previous + 1));
    }

    @Test
    public void orderWithLabelRepeatedInTheSameBranch() {
        int previous = orderDAO.getOrders().size();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.name = "Order name";
        orderDTO.code = "order-code";
        orderDTO.initDate = new Date();

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.name = "Label name";
        labelDTO.type = "Label type";
        orderDTO.labels.add(labelDTO);

        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.name = "Order line";
        orderLineDTO.code = "order-line-code";
        HoursGroupDTO hoursGroupDTO = new HoursGroupDTO("hours-group",
                ResourceEnumDTO.WORKER, 1000);
        orderLineDTO.hoursGroups.add(hoursGroupDTO);
        orderLineDTO.labels.add(labelDTO);
        orderDTO.children.add(orderLineDTO);

        List<InstanceConstraintViolationsDTO> instanceConstraintViolationsList = orderElementService
                .addOrder(orderDTO).instanceConstraintViolationsList;
        assertThat(instanceConstraintViolationsList.size(), equalTo(1));

        List<ConstraintViolationDTO> constraintViolations = instanceConstraintViolationsList
                .get(0).constraintViolations;
        // Mandatory fields: checkConstraintLabelNotRepeatedInTheSameBranch
        assertThat(constraintViolations.size(), equalTo(1));
        assertNull(constraintViolations.get(0).fieldName);

        assertThat(orderDAO.getOrders().size(), equalTo(previous));
    }

}