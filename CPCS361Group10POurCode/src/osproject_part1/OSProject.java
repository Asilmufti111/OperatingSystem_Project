//CPCS361 operating system project
//group 10: Asil and Dareen
//section: DAR

package osproject_part1;

import java.io.*;
import java.util.*;

public class OSProject {

    static PrintWriter print;
    public static int i; // internal event 
    public static int e; // external event
    public static int currentTime;  // system current time
    public static int avbMemory;  // available main memory
    public static int avbDevices;  // available devices
    public static int startingTime;  // system start time
    public static int mainMemorySize;  // system main memory
    public static int devices;  // system total devices
    public static int jobNum; // counter of the number of jobs
    public static int Quantum;//Quantum number
    public static int SR; // to store sum of burst times
    public static int AR; // to store remaining average of burst times
    public static int infi; // to store 999999
    public static Job JobInCPU = null; // jobs in cpu 

    static Queue<Job> submitQ = new LinkedList<Job>();
    static Queue<Job> completeQ = new LinkedList<Job>();
    static Queue<Job> readyQ = new LinkedList<Job>();

    // Priority queues to hold jobs Job scheduling for Hold Queues 2 is First In First Out (FIFO) based on arrival time.
    public static PriorityQueue<Job> HoldQueue2 = new PriorityQueue<Job>(new Comparator<Job>() {
        @Override
        public int compare(Job o1, Job o2) {
            if (o1.getArrivingTime() < o2.getArrivingTime()) {
                return -1;
            } else {
                return 1;
            }
        }
    });
    //Job scheduling for Hold Queue 1 is Based on the Requested Units of Main Memory (Ascending Order). 
    public static PriorityQueue<Job> HoldQueue1 = new PriorityQueue<Job>(new Comparator<Job>() {
        @Override
        public int compare(Job o1, Job o2) {
            if (o1.getRequestedMemory() == o2.getRequestedMemory() && o1.getPriority() == o2.getPriority()) {
                if (o1.getArrivingTime() < o2.getArrivingTime()) {
                    return -1; // o1 should come before o2
                } else if (o1.getArrivingTime() > o2.getArrivingTime()) {
                    return 1; // o1 should come after o2
                }
                return 0; // they are equal

            } else if (o1.getRequestedMemory() < o2.getRequestedMemory()) {
                return -1; // o1 should come before o2
            } else {
                return 1; // o1 should come after o2
            }
        }

    });

    public static void main(String[] args) throws FileNotFoundException {
        File infile = new File("inputnew3.txt");
        File output = new File("outputnew3_p1.txt");
        print = new PrintWriter(output);
        try (Scanner input = new Scanner(infile)) {

            while (input.hasNext()) {

                // Read C (system configuration)
                String line = input.nextLine().replaceAll("[a-zA-Z]=", "");
                if (line.isEmpty()) {
                    continue;
                }
                // Separate the info in an array
                String[] command = line.split(" ");
                // Read each line and set as variables in an array of commands, starting with (C) >> system config
                startingTime = Integer.parseInt(command[1]);
                mainMemorySize = Integer.parseInt(command[2]);
                devices = Integer.parseInt(command[3]);
                currentTime = startingTime; // Initialize current time
                avbMemory = mainMemorySize;
                avbDevices = devices;
                jobNum = 0;
                while (input.hasNextLine()) {
                    // Read line by line from the input file
                    line = input.nextLine().replaceAll("[a-zA-Z]=", "");
                    // Separate the info in an array
                    command = line.split(" ");
                    // Read each line and set as variables in an array of commands, after (C)

                    // Read (A) (job arrival)
                    if (command[0].equals("A")) {
                        int arrivingTime = Integer.parseInt(command[1]);
                        int jobNo = Integer.parseInt(command[2]);
                        int requestedMM = Integer.parseInt(command[3]);
                        int requestedDevices = Integer.parseInt(command[4]);
                        int burstTime = Integer.parseInt(command[5]);
                        int JobPriority = Integer.parseInt(command[6]);

                        // Create process for all valid jobs then add them to all_jobs queue
                        if (requestedMM <= mainMemorySize && requestedDevices <= devices) {
                            submitQ.add(new Job(arrivingTime, jobNo, requestedMM, requestedDevices, burstTime, JobPriority));
                            jobNum++; // To count the number of job entered to the queue
                        }
                        // Read (D) (display)
                    } else if (command[0].equals("D")) {
                        int time = Integer.parseInt(command[1]);
                        if (time < 999999) {
                            submitQ.add(new Job(time, -1));
                        } else {
                            infi = time;
                            break;
                        }
                    }
                }

                // Enter the first job to the CPU
                currentTime = submitQ.peek().getArrivingTime();
                avbMemory -= submitQ.peek().getRequestedMemory();
                avbDevices -= submitQ.peek().getRequestedDevice();
                Quantum = submitQ.peek().getBusrtTime();
                INTO_CPU(submitQ.poll());

                i = 0;
                e = 0;

                while (completeQ.size() != jobNum) {

                    if (submitQ.peek() != null) {
                        i = submitQ.peek().getArrivingTime();
                    } else {
                        i = 999999;
                    }

                    if (JobInCPU != null) {
                        e = JobInCPU.getFinishTime();
                    } else {
                        e = 999999;
                    }
                    currentTime = Math.min(i, e);

                    if (i == e) {
                        InternalEvent();
                        ExternalEvent();
                    } else if (e < i) {
                        InternalEvent();
                    } else {
                        ExternalEvent();
                    }

                }

                Display(999999, print);
                completeQ.clear();
            }
            print.close();
        }
    }

    // Method to put a job into CPU
    public static void INTO_CPU(Job job) {
        JobInCPU = job;
        JobInCPU.setStartTime(currentTime);
        Update_SRAR();

        //CPU execute P by TQ time 
        if (JobInCPU.getBusrtTime() <= Quantum) {
            JobInCPU.setFinishTime(currentTime + JobInCPU.getBusrtTime());
        } else {
            JobInCPU.setFinishTime(currentTime + Quantum);
        }

        // Update acuuredT
        JobInCPU.setAccuredT(JobInCPU.getFinishTime() - JobInCPU.getStartTime());
    }

    // Internal Event
    private static void InternalEvent() {

        OUT_CPU(); // Release
        OUT_HoldQs();

        if (!readyQ.isEmpty()) {
            INTO_CPU(readyQ.poll());
            Update_SRAR();

            if (!readyQ.isEmpty()) {
                Quantum = AR;
            } else {
                Quantum = JobInCPU.getBusrtTime();
            }
        }
    }

    // Method to calculate remaining average burst times
    public static void Update_SRAR() {

        if (readyQ.isEmpty()) {
            AR = 0;
        } else {
            SR = 0;
            for (Job job : readyQ) {
                SR += job.getBusrtTime();
            }
            AR = SR / readyQ.size();
        }
    }

    // External Event
    private static void ExternalEvent() {

        if (!submitQ.isEmpty()) {

            if (submitQ.peek().getJobNumber() == -1) {

                if (submitQ.poll().getArrivingTime() < 999999) {
                    Display(currentTime, print);
                }
            } else {
                Job j1 = submitQ.poll();
                if (j1.getRequestedMemory() <= avbMemory && j1.getRequestedDevice() <= avbDevices) {
                    avbMemory -= j1.getRequestedMemory();
                    avbDevices -= j1.getRequestedDevice();
                    readyQ.add(j1);
                    Update_SRAR();
                } else if (j1.getPriority() == 1) {
                    HoldQueue1.add(j1);
                } else {
                    HoldQueue2.add(j1);
                }
            }
        }

    }

    // Method to remove job from CPU
    private static void OUT_CPU() {

        if (JobInCPU != null) {
            // Update B.T
            JobInCPU.setBusrtTime(JobInCPU.getBusrtTime() - JobInCPU.getAccuredT());
            // If(P is terminated), decrease requested MM Qsize 
            // Then add it into complete queue
            if (JobInCPU.getBusrtTime() <= 0) {

                avbMemory += JobInCPU.getRequestedMemory();
                avbDevices += JobInCPU.getRequestedDevice();
                JobInCPU.setTurnAT(JobInCPU.getFinishTime() - JobInCPU.getArrivingTime());
                completeQ.add(JobInCPU);

            } // Return P to the ready queue with its updated burst time 
            else {
                readyQ.add(JobInCPU);
            }
            Update_SRAR();
        }
        if (!readyQ.isEmpty()) {
            JobInCPU = null;
        }
    }

    // Method to remove job from Hold queues
    private static void OUT_HoldQs() {
        int Qsize = HoldQueue1.size();
        int Counter = 0;
        // If the Hold queue is not empty 
        while (Counter < Qsize) {
            // Remove the first process from hold 1
            Job Job = HoldQueue1.remove();
            if (Job.getRequestedMemory() <= avbMemory && Job.getRequestedDevice() <= avbDevices) {
                avbMemory -= Job.getRequestedMemory();
                avbDevices -= Job.getRequestedDevice();
                readyQ.add(Job);
            } else {
                HoldQueue1.add(Job);
            }
            Counter++;
        } // end while HoldQ1
        // Return all processes from Store to hold 1
        // Check if Hold2 queue is not empty and the requested MM Qsize less than or equal unused memory 
        Qsize = HoldQueue2.size();
        Counter = 0;
        while (Counter < Qsize) {
            // Remove the first process from hold 2
            Job Job = HoldQueue2.remove();
            if (Job.getRequestedMemory() <= avbMemory && Job.getRequestedDevice() <= avbDevices) {
                avbMemory -= Job.getRequestedMemory();
                avbDevices -= Job.getRequestedDevice();
                readyQ.add(Job);
            } else {
                HoldQueue2.add(Job);
            }
            Counter++;
        } // end while HoldQ2
    }

    // Method to display system status
    private static void Display(int currentTime, PrintWriter print) {
        if (currentTime == 999999) {
            print.println("<< Final state of system: ");
        } else {
            print.println("<< At time " + currentTime);
        }
        print.println("Available Memory Size: " + avbMemory + "\t\t Available No of Devices: " + avbDevices);
        print.println("-----------------------------------------------------------------------\n");
        print.println("|Process\t |Burst Time\t |Arrival Time\t |Finish Time  \t |Turnaround Time\t |Waiting Time\t ");
        print.println("----------------------------------------------------------------------------------------------------------");

        for (Job job : completeQ) {
            print.println("  " + job.getJobNumber() + "\t\t    " + job.getCONbusrtTime() + "\t\t    " + job.getArrivingTime() + "\t\t    " + job.getFinishTime() + "\t\t     " + job.getTurnAT() + "\t\t\t   " + (job.getTurnAT() - job.getCONbusrtTime()));
        }
        print.println("----------------------------------------------------------------------------------------------------------\n");

        if (currentTime == 999999) {
            double system_TAT = 0.0;

            for (Job job : completeQ) {
                system_TAT += job.getTurnAT();
            }
            print.println("System Turnaround Time: " + system_TAT / jobNum);

        } else {

            print.println("Process running on the CPU: ");
            print.println("-----------------------------");
            print.println("Job ID   Run Time   Time Left");
            print.println("  " + JobInCPU.getJobNumber() + "           " + JobInCPU.getAccuredT() + "       " + JobInCPU.getBusrtTime() + "\n");

            print.print("Content of Ready Queue: ");
            for (Job job : readyQ) {
                print.print(job.getJobNumber() + ", ");
            }
            print.println("");

            print.print("Content of Hold Queue1: ");
            for (Job job : HoldQueue1) {
                print.print(job.getJobNumber() + ", ");
            }
            print.println("");

            print.print("Content of Hold Queue2: ");
            for (Job job : HoldQueue2) {
                print.print(job.getJobNumber() + ", ");
            }
        }
        print.println("\n**************************************************************************************************\n\n");
    }
}
