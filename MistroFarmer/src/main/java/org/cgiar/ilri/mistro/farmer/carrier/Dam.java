package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by jason on 8/5/13.
 */
public class Dam extends Cow implements Serializable
{
    public static final int SERVICE_TYPE_COW=0;
    public static final int SERVICE_TYPE_ET=1;
    private int serviceType;
    private String embryoNumber;
    private String vetUsed;

    public Dam()
    {
        super();
        setSex(SEX_FEMALE);
    }

    public Dam(Parcel source)
    {
        //super(source);
        readFromParcel(source);
    }

    public void setServiceType(int serviceType)
    {
        this.serviceType = serviceType;
    }

    public void setEmbryoNumber(String embryoNumber)
    {
        this.embryoNumber = embryoNumber;
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
        dest.writeString(embryoNumber);
        dest.writeString(vetUsed);
    }


    @Override
    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        serviceType=in.readInt();
        embryoNumber=in.readString();
        vetUsed=in.readString();
    }

    public static final Creator<Dam> CREATOR = new Creator<Dam>()
    {
        @Override
        public Dam createFromParcel(Parcel source)
        {
            return new Dam(source);
        }

        @Override
        public Dam[] newArray(int size)
        {
            return new Dam[size];
        }
    };


}
