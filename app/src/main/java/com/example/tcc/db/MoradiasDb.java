package com.example.tcc.db;

import com.example.tcc.db.models.Moradias;

import java.util.ArrayList;
import java.util.List;

public class MoradiasDb {
    public static List<Moradias> myDataset ;

    public MoradiasDb(List<Moradias> myDataset) {
         this.myDataset=myDataset;
    }

    public static List<Moradias> getMyDataset() {
        return myDataset;
    }
}
