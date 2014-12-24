package org.red5.demos.oflaDemo;

import java.util.Map;

public interface IDemoService {

	/**
     * Getter for property 'listOfAvailableFLVs'.
     *
     * @return Value for property 'listOfAvailableFLVs'.
     */
    public Map<String, Map<String, Object>> getListOfAvailableFLVs();

    public Map<String, Map<String, Object>> getListOfAvailableFLVs(String string);

}
