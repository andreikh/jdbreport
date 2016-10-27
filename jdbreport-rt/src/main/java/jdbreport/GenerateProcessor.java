/*
 * Created	23.10.2016
 *
 * JDBReport Generator
 *
 * Copyright (C) 2016 Andrey Kholmanskih
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
package jdbreport;

import jdbreport.design.model.TemplateBook;
import jdbreport.model.ReportBook;

/**
 * @author Andrey Kholmanskih
 * @version	3.1.3 23.10.2016
 */
public interface GenerateProcessor {

    void beforeGenerate(TemplateBook templateBook);

    void afterGenerate(ReportBook reportBook);
}
