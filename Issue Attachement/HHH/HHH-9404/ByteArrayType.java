package org.hibernate.type;

import java.util.Comparator;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

import com.google.common.primitives.UnsignedBytes;

public class ByteArrayType extends AbstractSingleColumnStandardBasicType<byte[]> implements VersionType<byte[]> {
    public ByteArrayType() {
        super(VarbinaryTypeDescriptor.INSTANCE, CustomPrimitiveByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "hb_binary";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[] { getName(), "byte[]", byte[].class.getName() };
    }

    @Override
    public byte[] seed(final SessionImplementor session) {
        return null;
    }

    @Override
    public byte[] next(final byte[] current, final SessionImplementor session) {
        return current;
    }

    @Override
    public Comparator<byte[]> getComparator() {
        return CustomPrimitiveByteArrayTypeDescriptor.INSTANCE.getComparator();
    }

    private static class CustomPrimitiveByteArrayTypeDescriptor extends PrimitiveByteArrayTypeDescriptor {
        public static final CustomPrimitiveByteArrayTypeDescriptor INSTANCE = new CustomPrimitiveByteArrayTypeDescriptor();

        @Override
        public Comparator<byte[]> getComparator() {
            return UnsignedBytes.lexicographicalComparator();
        }
    }

}
