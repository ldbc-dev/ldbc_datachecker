package com.ldbc.datachecker;

import gnu.trove.set.TLongSet;
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

    public abstract void add( T columnValue );

    public abstract boolean contains( T columnValue );

    public static class NothingColumnRef<T1> extends ColumnRef<T1>
    {
        public NothingColumnRef( String name )
        {
            super( name );
        }

        @Override
        public void add( T1 columnValue )
        {
        }

        @Override
        public boolean contains( T1 columnValue )
        {
            return true;
        }
    }

    public static class LongColumnRef extends ColumnRef<Long>
    {
        private final TLongSet set = new TLongHashSet();

        public LongColumnRef( String name )
        {
            super( name );
        }

        @Override
        public void add( Long value )
        {
            set.add( value );
        }

        @Override
        public boolean contains( Long value )
        {
            return set.contains( value );
        }

    }
}
