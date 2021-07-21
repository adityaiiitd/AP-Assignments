//Assignment 1
import java.util.ArrayList;
import java.util.Scanner;

class Cordinator{




    private ArrayList<Patients> listpatients;
    private ArrayList<Healthcare> listhealthcare;


    private ArrayList<Integer> IDremoved = new ArrayList<>();
    private ArrayList<Integer> showStopper = new ArrayList<>();
    private ArrayList<String> AlreadyPrinted = new ArrayList<>();



    public void setListhealthcare(ArrayList<Healthcare> listhealthcare) {
        this.listhealthcare = listhealthcare;
    }
    Cordinator(ArrayList<Patients> listpatients, ArrayList<Healthcare> listhealthcare){
        this.listpatients = listpatients;
        this.listhealthcare = listhealthcare;
    }
    public ArrayList<Integer> getShowStopper() {
        return showStopper;
    }
    public boolean isnotPresent(int ID){
        if(IDremoved==null) {
            return true;
        }
        for(Integer i: IDremoved){
            if (i==ID)
                return false;
        }
        return true;
    }
    public void allocatePatients(int[] arr){
        int k = 0;
        for(Healthcare heal: listhealthcare){
            while (heal.getStatus().equals("OPEN") && IDremoved.size()!=listpatients.size()){
                int maxOXY = Integer.MIN_VALUE;
                int index = -1;
                for(int i=0;i<listpatients.size();++i){
                    //find the patient to be added, map him with his recovery days.
                    if(listpatients.get(i).getOxylevel() >= heal.getOxylevel() && maxOXY < listpatients.get(i).getOxylevel() &&
                            isnotPresent(listpatients.get(i).getID())){
                        maxOXY = listpatients.get(i).getOxylevel();
                        index = i;
                    }
                }
                if(index==-1){
                    //then dont add anyone;
                    float bodyTemp = Float.MAX_VALUE;
                    int hisindex = -1;
                    for(int i=0;i<listpatients.size();++i){
                        if(listpatients.get(i).getTemperature()<bodyTemp && isnotPresent(listpatients.get(i).getID())){
                            bodyTemp = listpatients.get(i).getTemperature();
                            hisindex = i;
                        }
                    }
                    listpatients.get(hisindex).setAdmissionStatus("Admitted");
                    listpatients.get(hisindex).setHealthcareinstiture(heal.getName());
                    listpatients.get(hisindex).setDays(arr[k]);
                    System.out.println("Patient with id "+listpatients.get(hisindex).getID()+" will recover in "+arr[k]+" days");
                    IDremoved.add(listpatients.get(hisindex).getID());
                    showStopper.add(listpatients.get(hisindex).getID());
                    heal.getAdmitted().add(listpatients.get(hisindex));
                }
                else{
                    listpatients.get(index).setAdmissionStatus("Admitted");
                    listpatients.get(index).setHealthcareinstiture(heal.getName());
                    listpatients.get(index).setDays(arr[k]);
                    System.out.println("Patient with id "+listpatients.get(index).getID()+" will recover in "+arr[k]+" days");
                    IDremoved.add(listpatients.get(index).getID());
                    showStopper.add(listpatients.get(index).getID());
                    heal.getAdmitted().add(listpatients.get(index));
                }
                k++;
                heal.setNumofbeds(heal.getNumofbeds()-1);
                heal.updateStatus();
            }
        }
    }
    public void removeAdmitted() {
        ArrayList<Patients> newList = new ArrayList<>();
        ArrayList<Patients> UpdateList = new ArrayList<>();
        for(Patients p : listpatients) {
            if(IDremoved.contains(p.getID())) {
                newList.add(p);
            }
            else {
                UpdateList.add(p);
            }
        }
        listpatients = UpdateList;
        System.out.println("The removed patients are: ");
        for(Patients p:newList){
            System.out.println(p.getID());
        }
        IDremoved.clear();
    }
    public void removeHealthcare(){
        ArrayList<Healthcare> updateList = new ArrayList<>();
        ArrayList<Healthcare> newlist = new ArrayList<>();
        for (Healthcare healthcare : listhealthcare) {
            if (healthcare.getStatus().equals("CLOSED")) {
                newlist.add(healthcare);
            }
            else{
                updateList.add(healthcare);
            }
        }
        listhealthcare = updateList;
        System.out.println("They removed institutes are: ");
        for(Healthcare institute: newlist){
            if(!AlreadyPrinted.contains(institute.getName())) {
                System.out.println(institute.getName());
                AlreadyPrinted.add(institute.getName());
            }
        }
    }
    public void displayPatients(){
        for(Patients p: listpatients){
            System.out.println("Patient "+p.getName()+" has ID "+p.getID());
        }
    }
    public int inCampPatients(){
        int count=0;
        for(Patients p: listpatients){
            if(p.getAdmissionStatus().equals("Not Admitted")){
                count++;
            }
        }
        System.out.println(count);
        return count;
    }
    public void admittingInstitutes(){
        int count=0;
        for(Healthcare h : listhealthcare){
            if(h.getStatus().equals("OPEN")){
                count++;
            }
        }
        System.out.println(count);
    }
    public void getDetailsofHealthcare(String name){
        for(Healthcare h: listhealthcare){
            if (h.getName().equals(name)){
                h.displayDetails();
                break;
            }
        }
    }
    public void getDetailsofPatient(int ID){
        for(Patients p: listpatients){
            if(p.getID()==ID){
                p.displayDetils();
            }
        }

    }
    public void patientsAdmitted(String name){
        for(Healthcare h: listhealthcare){
            if(h.getName().equals(name)){
                h.displayPatients();
            }
        }
    }
}




class Healthcare {

    private final String name;
    private final float temperature;
    private final int oxylevel;

    public ArrayList<Patients> getAdmitted() {
        return Admitted;
    }

    private ArrayList<Patients> Admitted = new ArrayList<>();

    public void setNumofbeds(int numofbeds) {
        this.numofbeds = numofbeds;
    }

    private int numofbeds;
    private String status;



    Healthcare(String name, float temperature, int oxylevel, int numofbeds) {
        this.name = name;
        this.temperature = temperature;
        this.oxylevel = oxylevel;
        this.numofbeds = numofbeds;
        updateStatus();
    }
    public void displayDetails(){
        System.out.println(name);
        System.out.println("Temperature should be <= "+temperature);
        System.out.println("Oxygen level should be >= "+oxylevel);
        System.out.println("Number of available beds = "+numofbeds);
        System.out.println("Admission status is "+status);
    }

    public void displayPatients(){
        for(Patients p: Admitted){
            System.out.println(p.getName()+" will recover in "+p.getDays()+" days");
        }
    }
    public void updateStatus(){
        if(numofbeds>0) {
            status = "OPEN";
        }else{
            status = "CLOSED";
        }
    }
    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getOxylevel() {
        return oxylevel;
    }

    public int getNumofbeds() {
        return numofbeds;
    }

}


class Patients{

    private final String name;
    private final float temperature;
    private final int oxylevel;
    private final int age;
    private final int ID;

    public String getHealthcareinstiture() {
        return healthcareinstiture;
    }

    public void setHealthcareinstiture(String healthcareinstiture) {
        this.healthcareinstiture = healthcareinstiture;
    }

    private String healthcareinstiture = null;

    public void setAdmissionStatus(String admissionStatus) {
        this.admissionStatus = admissionStatus;
    }

    public String getAdmissionStatus() {
        return admissionStatus;
    }

    private String admissionStatus = "Not Admitted";
    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    private int days = -1;

    Patients(String name, float temperature, int oxylevel, int age){
        this.name = name;
        this.temperature = temperature;
        this.oxylevel = oxylevel;
        this.age = age;
        ID = this.hashCode();
    }
    public void displayDetils() {
        System.out.println("ID number :" + ID);
        System.out.println("Temperature is :" + temperature);
        System.out.println("Oxygen level is :" + oxylevel);
        System.out.println("Admission status is :" + admissionStatus);
        if (!(healthcareinstiture == null)) {
            System.out.println("Admitted in healthcare: " + healthcareinstiture);
        }
    }
    public String getName() {
        return name;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getOxylevel() {
        return oxylevel;
    }

    public int getAge() {
        return age;
    }

    public int getID(){
        return ID;
    }

}

public class Notmain {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int q = in.nextInt();
        ArrayList<Patients> patients = new ArrayList<>();
        ArrayList<Healthcare> healthcare = new ArrayList<>();
        for(int i=0;i<q;++i){
            String n = in.next();
            float t = in.nextFloat();
            int o = in.nextInt();
            int a = in.nextInt();
            patients.add(new Patients(n,t,o,a));
        }
        Cordinator cordinator = new Cordinator(patients,healthcare);
        while(patients.size()!=cordinator.getShowStopper().size()){
            int w = in.nextInt();
            if(w==1){
                String hospital_name = in.next();
                float max_temperature = in.nextFloat();
                int oxygen_level = in.nextInt();
                int num_of_beds = in.nextInt();
                int[] arr = new int[num_of_beds];
                for(int i=0;i<num_of_beds;++i){
                    arr[i] = in.nextInt();
                }
                Healthcare institute = new Healthcare(hospital_name,max_temperature,oxygen_level,num_of_beds);
                healthcare.add(institute);
                cordinator.setListhealthcare(healthcare);
                institute.displayDetails();
                cordinator.allocatePatients(arr);
            }
            if(w==2){
                cordinator.removeAdmitted();
            }
            if(w==3){
                cordinator.removeHealthcare();
            }
            if(w==4){
                cordinator.inCampPatients();
            }
            if(w==5){
                cordinator.admittingInstitutes();
            }
            if(w==6){
                String institure_name = in.next();
                cordinator.getDetailsofHealthcare(institure_name);
            }
            if(w==7){
                int num = in.nextInt();
                cordinator.getDetailsofPatient(num);
            }
            if(w==8){
                cordinator.displayPatients();
            }
            if(w==9){
                String nameofins = in.next();
                cordinator.patientsAdmitted(nameofins);
            }

        }
    }
}
