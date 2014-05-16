/*
 * JDBReport Generator
 *
 * Copyright (C) 2014 Andrey Kholmanskih. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 *
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.design.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @version 3.0 16.05.2014
 * @author Andrey Kholmanskih
 *
 */
public enum SystemVar {
    _PAGE, _ROW, _PAGE_COUNT; // "_СТРАНИЦА",  "_СТРОКА",  "_КОЛ_СТРАНИЦ"

    public String getLocalName() {
        String v = Messages.getString(name());
        if (v != null) return v;
        return name();
    }

    public Collection<String> names() {
        Set<String> vars = new HashSet<>();
        vars.add(name());
        vars.add(getLocalName());
        return vars;
    }

    public static SystemVar find(String value) {
        for (SystemVar var : SystemVar.values()) {
            if (var.name().equals(value)) {
                return var;
            }
        }
        for (SystemVar var : SystemVar.values()) {
            if (var.getLocalName().equals(value)) {
                return var;
            }
        }

        return null;
    }

    public static Set<String> getNames() {
        Set<String> vars = new HashSet<>();
        for (SystemVar var : values()) {
            vars.add(var.name());
            vars.add(var.getLocalName());
        }
        return vars;
    }
}
