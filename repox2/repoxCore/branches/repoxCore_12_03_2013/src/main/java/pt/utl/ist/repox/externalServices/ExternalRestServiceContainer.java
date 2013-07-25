package pt.utl.ist.repox.externalServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-12-2011
 * Time: 15:43
 */
public class ExternalRestServiceContainer {

    protected List<ExternalRestServiceThread> serviceThreads;
    protected String dataSourceId;
    private ExternalServiceStates.ServiceRunningState runningState = ExternalServiceStates.ServiceRunningState.START;
    private ExternalServiceStates.ServiceExitState exitState = ExternalServiceStates.ServiceExitState.NONE;
    private ExternalServiceStates.ContainerType containerType;

    public ExternalRestServiceContainer(ExternalServiceStates.ContainerType containerType) {
        serviceThreads = new ArrayList<ExternalRestServiceThread>();
        this.containerType = containerType;
    }

    public List<ExternalRestServiceThread> getServiceThreads(){
        return serviceThreads;
    }

    public void addExternalService(ExternalRestServiceThread externalRestServiceThread){
        serviceThreads.add(externalRestServiceThread);
        if(getContainerType().equals(ExternalServiceStates.ContainerType.PARALLEL))
            externalRestServiceThread.start();
        else
            externalRestServiceThread.run();
    }

    public String getDataSourceId(){
        return dataSourceId;
    }

    public ExternalServiceStates.ServiceRunningState getContainerRunningState(){
        return runningState;
    }

    public ExternalServiceStates.ServiceExitState getContainerExitState(){
        return exitState;
    }

    public ExternalServiceStates.ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ExternalServiceStates.ContainerType containerType) {
        this.containerType = containerType;
    }

    public void updateContainerState(){
        // todo detects errors / timeouts - register info at the log file

        boolean allFinished = false;
        for(ExternalRestServiceThread externalRestServiceThread: serviceThreads){
            if(externalRestServiceThread.getRunningState().equals(ExternalServiceStates.ServiceRunningState.RUNNING)){
                allFinished = false;
                break;
            } else
                allFinished = true;
        }

        if(allFinished){
            setContainerExitState();
            runningState = ExternalServiceStates.ServiceRunningState.FINISHED;
        }
    }

    private void setContainerExitState(){
        for(ExternalRestServiceThread externalRestServiceThread: serviceThreads){
            if(externalRestServiceThread.getExitState().equals(ExternalServiceStates.ServiceExitState.ERROR)){
                exitState = ExternalServiceStates.ServiceExitState.ERROR;
                break;
            } else
                exitState = ExternalServiceStates.ServiceExitState.SUCCESS;
        }
    }
}
