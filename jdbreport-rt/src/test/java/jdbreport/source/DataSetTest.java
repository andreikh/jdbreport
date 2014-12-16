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
package jdbreport.source;

import jdbreport.model.ReportException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Author: andrey
 * Date: 16.12.14
 */
public class DataSetTest {


    @Test
    public void loadDataTest() throws ReportException {
        List<Data> list = new ArrayList<>();
        list.add(new Data(1, "First data", true));
        list.add(new Data(2, "Second data", false));
        list.add(new Data(3, "Third data", true));

        IterableDataSet ds = new IterableDataSet("data", list);
        BufferedDataSet bufDs = new BufferedDataSet(ds);
        bufDs.open();
        assertEquals(1, bufDs.getValue("id"));
        assertEquals("First data", bufDs.getValue("name"));
        assertEquals(true, bufDs.getValue("enable"));
        assertTrue(bufDs.next());
        assertEquals(2, bufDs.getValue("id"));
        assertEquals("Second data", bufDs.getValue("name"));
        assertEquals(false, bufDs.getValue("enable"));
        assertTrue(bufDs.next());
        assertEquals(3, bufDs.getValue("id"));
        assertEquals("Third data", bufDs.getValue("name"));
        assertEquals(true, bufDs.getValue("enable"));
        assertFalse(bufDs.next());
    }

    public static class Data {

        private int id;

        private String name;

        private boolean enable;

        public Data(int id, String name, boolean enable) {
            this.id = id;
            this.name = name;
            this.enable = enable;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

}
