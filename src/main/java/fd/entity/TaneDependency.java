package fd.entity;

public class TaneDependency {
    private Integer dependeeIdx;
    private Integer dependerIdx;

    public TaneDependency(Integer dependeeIdx, Integer dependerIdx) {
        this.dependeeIdx = dependeeIdx;
        this.dependerIdx = dependerIdx;
    }

    public Integer getDependeeIdx() {
        return dependeeIdx;
    }

    public void setDependeeIdx(Integer dependeeIdx) {
        this.dependeeIdx = dependeeIdx;
    }

    public Integer getDependerIdx() {
        return dependerIdx;
    }

    public void setDependerIdx(Integer dependerIdx) {
        this.dependerIdx = dependerIdx;
    }
}
