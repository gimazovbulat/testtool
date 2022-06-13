package testtool;


import testtool.data.Status;

//system status listener
public interface SystemChangedListener {

    //	system status changed
    void systemChanged(Status old, Status current);
}
