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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ozsoft.secs4j.util.ConversionUtils;

/**
 * SECS data item F4 (sequence of 4-byte, single-precision floating-point
 * numbers).
 * 
 * @author Oscar Stigter
 */
public class F4 implements Data<List<Float>> {

    /** SECS name. */
    public static final String NAME = "F4";

    /** SECS format code. */
    public static final int FORMAT_CODE = 0x90;

    /** Fixed size in bytes. */
    public static final int SIZE = 4;

    /** The floating-point numbers. */
    private List<Float> values = new ArrayList<Float>();

    /**
     * Constructor with an initial empty sequence.
     */
    public F4() {
        // Empty implementation.
    }

    /**
     * Constructor with an initial single value.
     * 
     * @param value
     *            The value.
     */
    public F4(float value) {
        addValue(value);
    }

    /**
     * Constructor with an initial sequence with a single value, based on a raw byte buffer.
     * 
     * @param data
     *            The raw byte buffer containing a single value.
     */
    public F4(byte[] data) {
        this();
        addValue(data);
    }
    
    /**
     * Returns the value at a specific index position.
     * 
     * @param index
     *            The index position.
     * 
     * @return The value.
     */
    public float getValue(int index) {
        return values.get(index);
    }

    /**
     * Adds a value to the end of the sequence.
     * 
     * @param value
     *            The value to add.
     */
    public void addValue(float value) {
        values.add(value);
    }

    /**
     * Adds a value to the end of the sequence, based on a raw byte buffer.
     * 
     * @param data
     *            The byte buffer containing a single value.
     */
    public void addValue(byte[] data) {
        if (data.length != SIZE) {
            throw new IllegalArgumentException(String.format("Invalid %s length: %d bytes", NAME, data.length));
        }
        addValue(Float.intBitsToFloat((int) ConversionUtils.bytesToUnsignedInteger(data)));
    }

    @Override
    public List<Float> getValue() {
        return values;
    }

    @Override
    public void setValue(List<Float> values) {
        this.values = values;
    }

    @Override
    public int length() {
        return values.size();
    }

    @Override
    public byte[] toByteArray() {
        // Determine length.
        int length = values.size() * SIZE;
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

            // Write length bytes.
            for (int i = 0; i < noOfLengthBytes; i++) {
            	baos.write(lengthBytes.get(noOfLengthBytes - i - 1));
            }

            // Write values.
            for (float value : values) {
                int bits = Float.floatToIntBits(value);
                baos.write(ConversionUtils.integerToBytes(bits, SIZE));
            }

            return baos.toByteArray();

        } catch (IOException e) {
            // This should never happen.
            throw new RuntimeException("Could not serialize data item", e);

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
        sb.append(indent).append("<").append(NAME);
        if (length > 0) {
            for (float value : values) {
                sb.append(' ');
                sb.append(value);
            }
        }
        sb.append('>');
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof F4) {
            F4 f4 = (F4) obj;
            int length = f4.length();
            if (length == values.size()) {
                for (int i = 0; i < length; i++) {
                    if (f4.getValue(i) != values.get(i)) {
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
