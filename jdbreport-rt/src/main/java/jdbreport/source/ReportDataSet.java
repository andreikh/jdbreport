/*
 * JDBReport Generator
 *
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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

import java.util.Collection;

import jdbreport.model.ReportException;

/**
 * @author Andrey Kholmanskih
 * @version 3.1 15.12.2014
 */
public interface ReportDataSet extends Cloneable {

    /**
     * @return DataSet id
     */
    String getId();

    /**
     * Moves the cursor on the next record (object) in the DataSet
     *
     * @return true if successful
     * @throws ReportException ReportException
     */
    boolean next() throws ReportException;

    /**
     * Returns value from the current record (object) by the name
     *
     * @param name -
     *             the column's name or the property's name
     * @return value by the name
     * @throws ReportException ReportException
     */
    Object getValue(String name) throws ReportException;

    Object getValue(Object current, String name) throws ReportException;

    boolean containsKey(String name);

    /**
     * Returns names' collection of columns or properties
     *
     * @return names of columns or properties
     * @throws ReportException ReportException
     */
    Collection<String> getColumnNames() throws ReportException;

    /**
     * Returns a current object, can be null
     *
     * @return - current object
     * @throws ReportException ReportException
     */
    Object getCurrentObject() throws ReportException;

    /**
     * Returns parameters
     *
     * @return parameters
     * @throws ReportException ReportException
     */
    DataSetParams getParams() throws ReportException;

    /**
     * Returns id of the leading DataSet
     *
     * @return masterId property
     */
    String getMasterId();

    /**
     * Reopens the DataSet when the MasterDataSet changes parameters
     *
     * @return true if not eof
     * @throws ReportException ReportException
     */
    boolean reopen() throws ReportException;

    Object clone();

    boolean hasNext();
}
