package com.castellanos94.fuzzylogicgp.core;

import java.io.File;

public interface IAlgorithm {
    void execute(NodeTree predicate);
    void exportResult(File file);
    ResultTask getResult();
}
