/*
 * JDBReport Generator
 *
 * Copyright (C) 2014 Andrey Kholmanskih
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
    _PAGE, _ROW, _PAGE_COUNT;

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
