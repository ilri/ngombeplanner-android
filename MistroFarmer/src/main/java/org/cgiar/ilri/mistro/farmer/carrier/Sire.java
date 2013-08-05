package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by jason on 8/5/13.
 */
public class Sire extends Cow implements Serializable
{
    public static final int SERVICE_TYPE_BULL=0;
    public static final int SERVICE_TYPE_AI=1;
    private int serviceType;
    private String strawNumber;
    private String vetUsed;

    public Sire()
    {
        super();
        setSex(SEX_MALE);
    }

    public Sire(Parcel source)
    {
        readFromParcel(source);
    }

    public void setServiceType(int serviceType)
    {
        this.serviceType = serviceType;
    }

    public void setStrawNumber(String strawNumber)
    {
        this.strawNumber = strawNumber;
    }

    public void setVetUsed(String vetUsed)
    {
        this.vetUsed = vetUsed;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(serviceType);
        dest.writeString(strawNumber);
        dest.writeString(vetUsed);
    }

    @Override
    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        serviceType=in.readInt();
        strawNumber=in.readString();
        vetUsed=in.readString();
    }

    public static final Creator<Sire> CREATOR=new Creator<Sire>()
    {
        @Override
        public Sire createFromParcel(Parcel source)
        {
            return new Sire(source);
        }

        @Override
        public Sire[] newArray(int size)
        {
            return new Sire[size];
        }
    };
}
