

package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.cfg.NamingStrategy;

/**
 * A naming strategy that wraps another naming strategy, but makes all the
 * identifiers created shrink to specified length limit. This is done in two phases:
 * <br>
 * 1) The identifier is split into "words" - either by camel case or other separators -
 * those words are then shortened to {@link #minimalWordLength} starting from the first word,
 * until the identifier is short enough.
 * 2) If preceding step does not make the identifier short enough, characters in
 * the middle of the identifier are removed and replaced by {@link #ellipsis} to make
 * the identifier short enough.
 * <br><br>
 * The naming strategy does not guarantee that the shortened identifiers won't conflict.
 * (which is theoretically impossible]
 * It just does a nice try.
 * @author Martin Cerny
 */
public class GenericLengthLimitedNamingStrategy implements NamingStrategy {

    /**
     * The length limit imposed on db identifiers
     */
    private int lengthLimit;

    /**
     * Default value of the length limit
     */
    public static final int defaultLimit = 30;

    /**
     * When truncating the identifier in the middle, those characters are used
     * to notate the place where the charachters were removed
     */
    public static final String ellipsis = "__";

    private final Logger logger = Logger.getLogger(getClass());

    private NamingStrategy baseStrategy;

    /**
     * Create the strategy copying given base strategy with given limit
     * @param lengthLimit
     */
    public GenericLengthLimitedNamingStrategy(NamingStrategy strategy, int lengthLimit) {
        this.lengthLimit = lengthLimit;
        baseStrategy = strategy;
    }

    /**
     * Create the strategy copying given base strategy with the {@link #defaultLimit}
     */
    public GenericLengthLimitedNamingStrategy(NamingStrategy strategy) {
        this.lengthLimit = defaultLimit;
        baseStrategy = strategy;
    }

    /*
     * Following two methods were taken from http://www.java2s.com/Code/Java/Data-Type/SplitsaStringbyCharactertypeasreturnedbyjavalangCharactergetTypechar.htm
     * and are to be accompanied by the followin notice:
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements.  See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership.
     * The ASF licenses this file to You under the Apache License, Version 2.0
     * (the "License"); you may not use this file except in compliance with
     * the License.  You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    /**
     * <p>Splits a String by Character type as returned by
     * <code>java.lang.Character.getType(char)</code>. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: the character of type
     * <code>Character.UPPERCASE_LETTER</code>, if any, immediately
     * preceding a token of type <code>Character.LOWERCASE_LETTER</code>
     * will belong to the following token rather than to the preceding, if any,
     * <code>Character.UPPERCASE_LETTER</code> token.
     * <pre>
     * StringUtils.splitByCharacterTypeCamelCase(null)         = null
     * StringUtils.splitByCharacterTypeCamelCase("")           = []
     * StringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * StringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     * @param str the String to split, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    /**
     * <p>
     * Splits a String by Character type as returned by
     * <code>java.lang.Character.getType(char)</code>. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: if <code>camelCase</code> is <code>true</code>,
     * the character of type <code>Character.UPPERCASE_LETTER</code>, if any,
     * immediately preceding a token of type
     * <code>Character.LOWERCASE_LETTER</code> will belong to the following
     * token rather than to the preceding, if any,
     * <code>Character.UPPERCASE_LETTER</code> token.
     *
     * @param str
     *          the String to split, may be <code>null</code>
     * @param camelCase
     *          whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, <code>null</code> if null String
     *         input
     * @since 2.4
     */
    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new String[0];
        }
        char[] c = str.toCharArray();
        List list = new ArrayList();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER
                    && currentType == Character.UPPERCASE_LETTER) {
                int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(new String[list.size()]);
    }


    /**
     * Does the step 2) of above mentioned shortening process - ie. takes enough
     * characters from the middle of the string and replaces them with {@link #ellipsis}
     * @param source
     * @param requiredLength
     * @return
     */
    protected String removeMiddle(String source, int requiredLength){
        if(source.length() <= requiredLength){
            return source;
        } else {
            int charsToRemove = source.length() - requiredLength + ellipsis.length();
            int charsLeftOnBothSides = (source.length() - charsToRemove) / 2;
            return source.substring(0,charsLeftOnBothSides) + ellipsis + source.substring(source.length() - charsLeftOnBothSides, source.length());
        }
    }

    /**
     * Minimal number of characters for word shortening
     */
    public static final int minimalWordLength = 4;

    /**
     * Shortens individual "words" (separated by camel case and other delimiters)
     * to {@link #minimalWordLength} until the string is short enough
     * @param source
     * @param requiredLength
     * @return
     */
    protected String shortenWordsInString(String source, int requiredLength){
            String[] parts = splitByCharacterTypeCamelCase(source);
            
            if(parts.length <= 1){
                return(source);
            }

            int totalCharsRemoved = 0;
            for(int i = 0; i < parts.length; i++){
                if(source.length() - totalCharsRemoved <= requiredLength){
                    break;
                }
                if(parts[i].length() > minimalWordLength){
                    totalCharsRemoved += parts[i].length() - minimalWordLength;
                    parts[i] = parts[i].substring(0,minimalWordLength);
                }
            }

            StringBuilder shortenedStringBuilder = new StringBuilder();
            for(int i = 0; i < parts.length; i++){
                shortenedStringBuilder.append(parts[i]);
            }
            return(shortenedStringBuilder.toString());
    }

    /**
     * Shortens given string to fit in the length limit
     * @param unshortened
     * @return
     */
    protected String shortenString(String unshortened){
        if(unshortened.length() <= lengthLimit){
            return unshortened;
        } else {
            String shortened = shortenWordsInString(unshortened, lengthLimit);

            if(shortened.length() > lengthLimit){
                shortened = removeMiddle(unshortened, lengthLimit);
            }
            logger.debug("Shortened DB identifier " + unshortened + " to " + shortened);
            return shortened;
        }
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param ownerEntity
     * @param ownerEntityTable
     * @param associatedEntity
     * @param associatedEntityTable
     * @param propertyName
     * @return
     */
    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return shortenString(baseStrategy.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param propertyName
     * @param propertyEntityName
     * @param propertyTableName
     * @param referencedColumnName
     * @return
     */
    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return shortenString(baseStrategy.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param columnName
     * @param propertyName
     * @param referencedColumn
     * @return
     */
    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return shortenString(baseStrategy.logicalCollectionColumnName(columnName, propertyName, referencedColumn));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param tableName
     * @param ownerEntityTable
     * @param associatedEntityTable
     * @param propertyName
     * @return
     */
    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        return shortenString(baseStrategy.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param columnName
     * @param propertyName
     * @return
     */
    @Override
    public String logicalColumnName(String columnName, String propertyName) {        
        return shortenString(baseStrategy.logicalColumnName(columnName, propertyName));
    }


    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param propertyName
     * @return
     */
    @Override
    public String propertyToColumnName(String propertyName) {
        return shortenString(baseStrategy.propertyToColumnName(propertyName));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param className
     * @return
     */
    @Override
    public String classToTableName(String className) {
        return shortenString(baseStrategy.classToTableName(className));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param columnName
     * @return
     */
    @Override
    public String columnName(String columnName) {
        return shortenString(baseStrategy.columnName(columnName));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param joinedColumn
     * @param joinedTable
     * @return
     */
    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return shortenString(baseStrategy.joinKeyColumnName(joinedColumn, joinedTable));
    }

    /**
     * Delegates the call to {@link #baseStrategy} and shortens the result
     * @param tableName
     * @return
     */
    @Override
    public String tableName(String tableName) {
        return shortenString(baseStrategy.tableName(tableName));
    }



}
