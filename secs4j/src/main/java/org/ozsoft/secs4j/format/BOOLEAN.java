// This file is part of the secs4j project, an open source SECS/GEM
// library written in Java.
//
// Copyright 2013 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.secs4j.format;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * SECS data item BOOLEAN (a single boolean).
 * 
 * @author Oscar Stigter
 */
public class BOOLEAN implements Data<List<Boolean>> {

    /** SECS format code. */
    public static final int FORMAT_CODE = 0x24;

    /** Fixed length of 1 byte. */
    public static final int LENGTH = 1;

    /** Byte value for FALSE. */
    public static final byte FALSE = 0x00;

    /** Byte value for TRUE. */
    public static final byte TRUE = 0x01;

    /** The boolean value. */
    private List<Boolean> values = new ArrayList<Boolean>();

    public BOOLEAN() {
    	
    }

    /**
     * Constructor based on a byte value.
     * 
     * @param b
     *            The byte value.
     */
    public BOOLEAN(byte b) {
        values.add(b == FALSE ? false : true);
    }

    /**
     * Constructor based on a byte value.
     * 
     * @param b
     *            The byte value.
     */
    public BOOLEAN(int b) {
    	values.add(b == FALSE ? false : true);
    }

    /**
     * Constructor with an initial value.
     * 
     * @param value
     *            The initial value.
     */
    public BOOLEAN(boolean value) {
    	values.add(value);
    }

    public void addValue(boolean value) {
    	values.add(value);
    }

    public void addValue(byte b) {
    	values.add(b == FALSE ? false : true);
    }

    public boolean getValue(int index) {
        return values.get(index);
    }

    @Override
    public List<Boolean> getValue() {
        return values;
    }

    @Override
    public void setValue(List<Boolean> values) {
        this.values = values;
    }

    @Override
    public int length() {
    	return values.size();
    }

    @Override
    public byte[] toByteArray() {
    	// Determine length.
        int length = length();
        int noOfLengthBytes = 1;
        B lengthBytes = new B();
        lengthBytes.add(length & 0xff);
        if (length > 0xff) {
            noOfLengthBytes++;
            lengthBytes.add((length >> 8) & 0xff);
        }
        if (length > 0xffff) {
            noOfLengthBytes++;
            lengthBytes.add((length >> 16) & 0xff);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // Write format byte.
            baos.write(FORMAT_CODE | noOfLengthBytes);
            for (int i = 0; i < noOfLengthBytes; i++) {
            	baos.write(lengthBytes.get(noOfLengthBytes - i - 1));
            }
            // Write bytes recursively.
            for (boolean value : values) {
                baos.write(value ? TRUE : FALSE);
            }
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    @Override
    public String toSml() {
        StringBuilder sb = new StringBuilder();
        toSml(sb, "");
        return sb.toString();
    }

    @Override
    public void toSml(StringBuilder sb, String indent) {
    	int length = length();
        sb.append(indent).append("<BOOLEAN");
        if (length > 0) {
            for (boolean value : values) {
                sb.append(' ');
                sb.append(value ? "1" : "0");
            }
        }
        sb.append('>');
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BOOLEAN) {
        	BOOLEAN b = (BOOLEAN) obj;
            int length = b.length();
            if (length == length()) {
                for (int i = 0; i < length; i++) {
                    if (b.getValue(i) != values.get(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toSml();
    }

}
