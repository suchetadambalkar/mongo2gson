/*
   Copyright [2012] [Entrib Technologies]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.entrib.mongo2gson;

import com.google.gson.*;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Utility class to convert Mongo Java objects into Google Json (Gson API) objects.
 * <p/>
 * This class has three APIs
 * <ul>
 *   <li>Convert given Mongo BasicDBList object to Google Gson JsonArray object</li>
 *   <li>Convert given Mongo BasicDBObject object to Google Gson JsonObject object</li>
 *   <li>Convert given primitive data object such as Long, Double, Boolean as well as String to Google Gson JsonPrimitive object</li> 
 * </ul>
 *
 * The APIs are recursive and hence support nested objects.
 *  
 * @author Atul M Dambalkar (atul@entrib.com)
 */
public final class Mongo2gson {

    /**
     * Convert the given mongo BasicDBList object to JsonArray.
     *
     * @param list BasicDBList
     * @return JsonArray
     */
    public static JsonArray getAsJsonArray(BasicDBList list) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            Object dbObject = list.get(i);
            if (dbObject instanceof BasicDBList) {
                jsonArray.add(getAsJsonArray((BasicDBList) dbObject));
            } else if (dbObject instanceof BasicDBObject) { // it's an object
                jsonArray.add(getAsJsonObject((BasicDBObject) dbObject));
            } else {   // it's a primitive type number or string 
                jsonArray.add(getAsJsonPrimitive(dbObject));
            }
        }
        return jsonArray;
    }

    /**
     * Convert the given mongo BasicDBObject to JsonObject.
     *
     * @param dbObject BasicDBObject
     * @return JsonObject
     */
    public static JsonObject getAsJsonObject(BasicDBObject dbObject) {
        Set<String> keys = dbObject.keySet();
        Iterator<String> iterator = keys.iterator();
        JsonObject jsonObject = new JsonObject();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object innerObject = dbObject.get(key);
            if (innerObject instanceof BasicDBList) {
                jsonObject.add(key, getAsJsonArray((BasicDBList)innerObject));
            } else if (innerObject instanceof BasicDBObject) {
                jsonObject.add(key, getAsJsonObject((BasicDBObject)innerObject));
            } else {
                jsonObject.add(key, getAsJsonPrimitive(innerObject));
            }
        }
        return jsonObject;
    }

    /**
     * Convert the given object to Json primitive JsonElement based on the type.
     * 
     * @param value Object
     * @return JsonElement
     */
    public static JsonElement getAsJsonPrimitive(Object value) {
        if (value instanceof String) {
            return new JsonPrimitive((String) value);
        } else if (value instanceof Long) {
            return new JsonPrimitive((Long) value);
        } else if (value instanceof Double) {
            return new JsonPrimitive((Double) value);
        } else if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        }
        throw new IllegalArgumentException("Unsupported value type for: " + value);
    }
}