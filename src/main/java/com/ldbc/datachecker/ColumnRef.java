package com.ldbc.datachecker;

import java.util.Collections;
import java.util.Comparator;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLongHashSet;

public abstract class ColumnRef<T>
{
    private final String name;

    public ColumnRef( String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }

    /**
     * @param columnValue
     * @return false if columnValue already existed, otherwise true
     */
    public abstract boolean add( T columnValue );

    public abstract boolean contains( T columnValue );

    public static class LongColumnRef extends ColumnRef<Long>
    {
        private final TLongSet set = new TLongHashSet();

        public LongColumnRef( String name )
        {
            super( name );
        }

        @Override
        public boolean add( Long value )
        {
            return set.add( value );
        }

        @Override
        public boolean contains( Long value )
        {
            return set.contains( value );
        }
    }

    public static class MultiLongColumnRef extends ColumnRef<Long>
    {
        private final THashSet<TLongList> set = new THashSet<TLongList>();
        private final int bufferSize;
        private final boolean doSort;
        private TLongList buffer = new TLongArrayList();

        public MultiLongColumnRef( String name, int bufferSize, boolean doSort )
        {
            super( name );
            this.bufferSize = bufferSize;
            this.doSort = doSort;
        }

        @Override
        public boolean add( Long value )
        {
            buffer.add( value );
            if ( buffer.size() == bufferSize )
            {
                TLongList entry = buffer;
                if ( doSort ) entry.sort();
                buffer = new TLongArrayList();
                return set.add( entry );
            }
            return true;
        }

        @Override
        public boolean contains( Long value )
        {
            return set.contains( value );
        }
    }
}
