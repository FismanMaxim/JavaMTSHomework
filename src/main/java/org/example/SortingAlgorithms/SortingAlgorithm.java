package org.example.SortingAlgorithms;

import org.example.SortingAlgorithmType;

import java.util.ArrayList;

public abstract class SortingAlgorithm {
  protected int maxListSize;

  public SortingAlgorithm(int maxListSize) {
    this.maxListSize = maxListSize;
  }

  public abstract SortingAlgorithmType getAlgorithmType();

  public abstract void sort(ArrayList<Integer> list);
}
