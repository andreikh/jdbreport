/*
 * Created on 01.07.2005
 *
 * Copyright 2006-2014 Andrey Kholmanskih
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
package jdbreport.util.finder;


/**
 * @version 1.0 24.06.2006
 * @author Andrey Kholmanskih
 *
 */
public class FindParams {

    public static final int FORWARD  = 0;
    public static final int BACKWARD = 1;
    public static final int SCOPE_ALL = 0;
    public static final int SCOPE_SELECTED = 1;
    
    private String findText;
    private int direction = FORWARD;
    private int scope = SCOPE_ALL;
    private boolean caseSensitive;
    private boolean wholeWord;
    private boolean wrapSearch;
    private boolean incremental;
    /* for table */
    private int column = -1;
    
    /**
     * 
     */
    public FindParams(String text, int direction, int scope, boolean incremental, 
            boolean case_sensitive, boolean wrap_search, boolean whole_word) {
        super();
        this.findText = text;
        this.direction = direction;
        this.scope = scope;
        this.caseSensitive = case_sensitive;
        this.incremental = incremental;
        this.wrapSearch = wrap_search;
        this.wholeWord = whole_word;
    }

    public FindParams(String text, int direction, int scope, boolean incremental, 
            boolean case_sensitive, boolean wrap_search) {
        this(text, direction, scope, incremental, case_sensitive, wrap_search, false);
    }
    
    public FindParams(String text, int direction, int scope, boolean incremental) {
        this(text, direction, scope, incremental, false, false, false);
    }
    
    public FindParams(String text, int direction, int scope) {
        this(text, direction, scope, false);
    }
    
    public FindParams(String text, int direction) {
        this(text, direction, SCOPE_ALL);
    }
    
    public FindParams(String text, int direction, int scope, int column) {
        this(text, direction, scope);
        this.column = column;
    }
    
    public FindParams(String text) {
        this(text, FORWARD);
    }

    /**
     * @return Returns the findText.
     */
    public String getFindText() {
        return findText;
    }

    /**
     * @return Returns the direction.
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @return Returns the scope.
     */
    public int getScope() {
        return scope;
    }

    /**
     * @return Returns the case_sensitive.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @return Returns the whole_word.
     */
    public boolean isWholeWord() {
        return wholeWord;
    }

    /**
     * @return Returns the wrap_search.
     */
    public boolean isWrapSearch() {
        return wrapSearch;
    }

    /**
     * @return Returns the incremental.
     */
    public boolean isIncremental() {
        return incremental;
    }

    /**
     * if true - search on all fields of the table
     * else search on a field column
     * @return
     */
    public boolean isAllColumn() {
        return column < 0;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    public int getColumn() {
        return column;
    }
}
