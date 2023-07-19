package com.example.ringlife;

import java.util.Vector;

public class LimitVector<Float> extends Vector<Float> {
    private int limit;

    public LimitVector(int limit) {
        super(limit);
        this.limit = limit;
    }

    public void addElementAtBeginning(Float element) {
        if (this.size() == this.limit) {
            this.removeElementAt(this.limit - 1);
        }
        this.insertElementAt(element, 0);
    }

    public float media() {
        float med = 0.0f;
        for(int i=0; i<this.size(); i++)
            med += (float)this.get(i);

        med /= this.size();
        return med;
    }

    public float deltaMedio() {
        float deltaM = 0.0f;
        int lastNum = 0;
        for(int i=0; i<this.size(); i++) {
            if ((i != 0 && (float)this.get(i) == 0) || (i == 0 && i<(this.size()-1) && (float)this.get(i+1) == 0)) {
                break;  // Interrompe il ciclo non appena viene trovato uno 0
            }
            lastNum++;
        }

        for(int i=0; i<lastNum-1; i++)
            deltaM += (float)this.get(i+1) - (float)this.get(i);

        deltaM /= lastNum;
        if(lastNum == 0) deltaM = 0;
        return deltaM;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i < this.size(); i++) {
            sb.append(this.elementAt(i));
            if(i < this.size()-1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

