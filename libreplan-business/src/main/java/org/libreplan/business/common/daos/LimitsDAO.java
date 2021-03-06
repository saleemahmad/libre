/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2015 LibrePlan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.business.common.daos;

import org.libreplan.business.common.entities.Limits;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for {@link Limits}
 *
 * Created by
 * @author Vova Perebykivskiy <vova@libreplan-enterprise.com>
 * on 24.09.15.
 */

@Repository
public class LimitsDAO extends GenericDAOHibernate<Limits, Long> implements ILimitsDAO {

    @Override
    public List<Limits> getAll() {
        return list(Limits.class);
    }

    @Override
    public Limits getUsersType() {
        List<Limits> list = list(Limits.class);
        for (Limits item : list)
            if (item.getType().equals("users")) return item;
        return null;
    }

    @Override
    public Limits getResourcesType() {
        List<Limits> list = list(Limits.class);
        for (Limits item : list)
            if (item.getType().equals("workers+machines")) return item;
        return null;
    }
}
