//CPCS361 operating system project
//group 10: Asil and Dareen
//section: DAR

package osproject_part2;

// Class representing a job in the operating system
public class Job {

    // Attributes of a job
    int arrivingTime;   // Time at which the job arrives
    int jobNumber;      // Unique identifier for the job
    int requestedMemory;    // Amount of memory requested by the job
    int requestedDevice;    // Number of devices requested by the job
    int priority;       // Priority of the job
    int busrtTime;      // Burst time of the job (time required to execute)
    int CONbusrtTime;   // Constant burst time (initial burst time)
    int startTime;      // Start time of the job execution
    int finishTime;     // Finish time of the job execution
    int accuredT;       // Time accrued during execution
    int turnAT;         // Turnaround time of the job

    // Constructor for a job with only arriving time and job number
    public Job(int arrivingTime, int jobNumber) {
        this.arrivingTime = arrivingTime;
        this.jobNumber = jobNumber;
    }

    // Constructor for a complete job with all attributes
    Job(int arrivingTime, int jobNumber, int requestedMemory, int requestedDevice, int busrtTime, int priority) {
        this.jobNumber = jobNumber;
        this.requestedMemory = requestedMemory;
        this.requestedDevice = requestedDevice;
        this.arrivingTime = arrivingTime;
        this.busrtTime = busrtTime;
        this.priority = priority;
        this.CONbusrtTime = busrtTime;
        this.accuredT = 0;
    }

    // Getters and setters for job attributes
    public void setBusrtTime(int busrtTime) {
        this.busrtTime = busrtTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setAccuredT(int accuredT) {
        this.accuredT = accuredT;
    }

    public int getCONbusrtTime() {
        return CONbusrtTime;
    }

    public int getArrivingTime() {
        return arrivingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getTurnAT() {
        return turnAT;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public int getRequestedMemory() {
        return requestedMemory;
    }

    public int getRequestedDevice() {
        return requestedDevice;
    }

    public int getPriority() {
        return priority;
    }

    public int getBusrtTime() {
        return busrtTime;
    }

    public int getAccuredT() {
        return accuredT;
    }

    public void setArrivingTime(int arrivingTime) {
        this.arrivingTime = arrivingTime;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setRequestedMemory(int requestedMemory) {
        this.requestedMemory = requestedMemory;
    }

    public void setRequestedDevice(int requestedDevice) {
        this.requestedDevice = requestedDevice;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setTurnAT(int turnAT) {
        this.turnAT = turnAT;
    }
}
