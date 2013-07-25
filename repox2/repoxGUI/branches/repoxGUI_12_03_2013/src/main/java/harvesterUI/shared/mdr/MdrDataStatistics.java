package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;
import harvesterUI.shared.dataTypes.SimpleDataSetInfo;

import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:46
 */
public class MdrDataStatistics implements IsSerializable{

    private int numberTimesUsed;
    private List<SimpleDataSetInfo> usedInList;

    public MdrDataStatistics() {
    }

    public MdrDataStatistics(int numberTimesUsed, List<SimpleDataSetInfo> usedInList) {
        this.numberTimesUsed = numberTimesUsed;
        this.usedInList = usedInList;
    }

    public int getNumberTimesUsed() {
        return numberTimesUsed;
    }

    public List<SimpleDataSetInfo> getUsedInList() {
        return usedInList;
    }
}
