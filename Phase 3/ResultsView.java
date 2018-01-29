

package org.tensorflow.aafvi;

import org.tensorflow.aafvi.Classifier.Recognition;

import java.util.List;

public interface ResultsView {
  public void setResults(final List<Recognition> results);
}
