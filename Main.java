import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;


//n - input which is num of players

// Types of players:
// commoner - do not know about any other people
// commoner has 1000 HP
// detective - knows about other detectives (n/5)
// detective has 800 hp
// healer - knows about other healers {max(1,n/10)};
// healer has 800 hp
// mafia - knows about other mafia and won't kill them (n/5)
// mafia has 2500 hp

// 1:1 ratio game ends:
// n>=6 initially


// how does the game work?


class MyGenericList<T>{
    private final ArrayList<T> list;
    MyGenericList(){
        list = new ArrayList<T>();
    }
    public void add(T other){
        list.add(other);
    }
    public T get(int index){
        return list.get(index);
    }
    public void print(){
        for(Object o:list){
            System.out.print(o +" ");
        }
        System.out.println();
    }
    public int size(){
        return list.size();
    }
    public void shuffleList(){
        Collections.shuffle(list);
    }
    public void remove(int rem){
        list.remove(rem);
    }
    public void remove(T player){
        list.remove(player);
    }

}
class Game{

    //so player is a cup and things that can adjust in it are Healers, Detectives, Commoners, Mafias
    private final ArrayList<Integer> playersAlivewithID = new ArrayList<>();//helper list
    private final ArrayList<Mafia> mafiaArrayList = new ArrayList<>();
    private final ArrayList<Healer> healerArrayList = new ArrayList<>();
    private final ArrayList<Detective> detectives = new ArrayList<>();
    private final ArrayList<Commoner> commonerArrayList = new ArrayList<>();
    private final MyGenericList<Player> playerArrayList = new MyGenericList<>();//main generic list which is like a cup that holds healer,mafia,detectives and commoners
    private final MyGenericList<Player> clonedPlayerlist = new MyGenericList<>();
    private final Player user;
    Game(Player user,int n){
        this.user = user;
        playerArrayList.add(user);
        user.setUserCheck(true);
        this.assignCharacters(n-1);
        this._startGame(user);
    }
    private int returnmaxVoted(int[] arr){
        int max = 0;
        int retindex = 0;
        for(int i=0;i<arr.length;++i){
            if(arr[i]>=max){
                retindex = i;
                max = arr[i];
            }
        }
        return retindex;
    }
    //this method basically askes all mafia's to vote
    //NOTE METHOD TO KILLPLAYER IS IN MAFIA CLASS ONLY, THIS USES ONLY ITS CALL FOR DIFFERENT MAFIA'S
    public int[] MafiaSelectTarget(int[] votesTokill){
        for (Mafia mafia : mafiaArrayList) {
            boolean voteFlag;
            voteFlag = true;
            while (voteFlag) {
                int num = mafia.killPlayer(playerArrayList.size());
                if (playerArrayList.get(num) instanceof Mafia) {
                    //do nothing
                } else {
                    votesTokill[num]++;
                    voteFlag = false;
                }
            }
        }
        return votesTokill;
    }
    private void removeDeadPlayer(Player who_died){
        if(who_died instanceof Healer){
            healerArrayList.remove((Healer) who_died);
        }
        else if(who_died instanceof Detective){
            detectives.remove((Detective) who_died);
        }
        else if(who_died instanceof Mafia){
            mafiaArrayList.remove((Mafia) who_died);
        }
        else{
            commonerArrayList.remove((Commoner) who_died);
        }
    }
    private Player getPlayerfromID(int t){
        for(int i=0;i<playerArrayList.size();++i){
            if(playerArrayList.get(i).getId() == t)
                return playerArrayList.get(i);
        }
        return null;
    }

    private void attackTarget(int voted){
        //if HP of mafia's is more than of the person they voted; reduce that person HP to 0;
        //else reduce that player's hp by sum of mafia's HP
        int maxHP = 0;
        int damageAbsorb = 0;
        int count = 0;
        for(Mafia m: mafiaArrayList){
            maxHP+=m.getHp();
        }
        boolean strongMafia = false;
        Player whoisvoted = getPlayerfromID(voted);
        int moreHealth = 0;
        for(Mafia m: mafiaArrayList){
            assert whoisvoted != null;
            if(m.compareTo(whoisvoted)>0)
                moreHealth++;
        }
        if(moreHealth == mafiaArrayList.size()){
            //all of the players have more hp then target
            //System.out.println("Even each mafia can kill target individually as-well ");
            strongMafia = true;
        }
        int x = whoisvoted.getHp();
        int y = mafiaArrayList.size();

        if(maxHP>=whoisvoted.getHp())whoisvoted.setHp(0);
        else{
            whoisvoted.setHp(whoisvoted.getHp()-maxHP);
        }
        //reduce the HP of mafia's;
        int countWeakMafia = 0;
        for(Mafia m:mafiaArrayList){
            if(m.getHp()-x/y<0){
                m.setHp(0);
                countWeakMafia++;
                damageAbsorb-=m.getHp()-x/y;
            }
            else{
                m.setHp(m.getHp()-x/y);
            }
        }
    }
    private int[] HealerHealPerson(int[] personToheal){
        for (Healer h : healerArrayList) {
            boolean voteFlag;
            voteFlag = true;
            while (voteFlag) {
                int num = h.healPlayer(playerArrayList.size());
                personToheal[num]++;
                voteFlag = false;
            }
        }
        return personToheal;

    }
    private int[] DetectiveChoosePerson(int[] votetoCheck){
        for (Detective d : detectives) {
            boolean voteFlag;
            voteFlag = true;
            while (voteFlag) {
                int num = d.checkPlayer(playerArrayList.size());
                if (playerArrayList.get(num) instanceof Detective) {
                    //do nothing
                } else {
                    votetoCheck[num]++;
                    voteFlag = false;
                }
            }
        }
        return votetoCheck;
    }
    private void printArraylist(ArrayList<?> list){
        for(int i=0;i<list.size();++i){
            System.out.print(list.get(i)+" ");
        }
        System.out.println();
    }

    private void printPlayerDetails(MyGenericList<Player> list){
        ArrayList<Mafia> m = new ArrayList<>();
        ArrayList<Healer> h = new ArrayList<>();
        ArrayList<Detective> d = new ArrayList<>();
        ArrayList<Commoner> c = new ArrayList<>();
        for(int i=0;i<list.size();++i){
            if(list.get(i).equals(user))
                user.setIsUser("[USER]");
            if(list.get(i) instanceof Mafia){
                m.add((Mafia)list.get(i));
            }
            if(list.get(i) instanceof Healer){
                h.add((Healer) list.get(i));
            }
            if(list.get(i) instanceof Detective){
                d.add((Detective) list.get(i));
            }
            if(list.get(i) instanceof Commoner){
                c.add((Commoner) list.get(i));
            }
        }
        System.out.print("Mafia's are ");printArraylist(m);
        System.out.print("Detectives are ");printArraylist(d);
        System.out.print("Healers are ");printArraylist(h);
        System.out.print("Commoners are ");printArraylist(c);
    }
    private boolean checkPerson(int voted){
        return playerArrayList.get(voted) instanceof Mafia;
    }
    private void healPerson(int voted){
        playerArrayList.get(voted).setHp(playerArrayList.get(voted).getHp()+500);
    }
    private int[] botVotePlayers(int[] voteList){
        Random ran = new Random();
        for(int i=0;i<playerArrayList.size();++i){
            Player p = playerArrayList.get(i);
            int val = p.votePlayer(playerArrayList.size());
            voteList[val]++;
        }
        return voteList;
    }
    private int getIndexOfPlayer(Player p){
        for(int i=0;i<playerArrayList.size();++i){
            if(p.getId()==playerArrayList.get(i).getId()){
                return i;
            }
        }
        return -1;
    }
    private void _startGame(Player user) {
        Scanner in = new Scanner(System.in);
        System.out.println("You are "+user.toString());
        boolean userStatus = true;
        int statusval = 1;
        //printPlayerDetails(clonedPlayerlist);
        if(user instanceof Mafia){
            System.out.print("You are a mafia, your other teammates are: ");
            for(Mafia mafia:mafiaArrayList){
                System.out.print(mafia.toString());
            }
            System.out.println();
            int round = 1;
            boolean flag = true;
            while (flag){
                if(1+mafiaArrayList.size()>=detectives.size()+healerArrayList.size()+commonerArrayList.size()){
                    System.out.println("Mafia's have won! GG");
                    printPlayerDetails(clonedPlayerlist);
                    break;
                }
                System.out.println("Round "+round+":");
                round++;


                System.out.print(playerArrayList.size()+" players are remaining: ");
                printPlayer();

                System.out.println("Choose a target: ");

                int user_voted = 0;
                boolean voteFlag = true;
                while (voteFlag){
                    user_voted = in.nextInt();
                    if(playersAlivewithID.contains(user_voted)){
                        Player p = getPlayerfromID(user_voted);
                        if(p instanceof Mafia){
                            System.out.println("You cannot kill yourself or your teammate");
                        }
                        else {
                            voteFlag = false;
                        }
                    }
                    else{
                        System.out.println("No such player exists, please vote again!");
                    }
                }
                int votedBymafia = user_voted;//id of the player
                boolean do_detective_check = false;
                if(detectives.size()>0){
                    do_detective_check = true;
                }

                int[] voteTocheck = new int[playerArrayList.size()];
                int votedTochecked = 0;
                //detective now selects a player to check;
                if(do_detective_check) {
                    DetectiveChoosePerson(voteTocheck);
                    votedTochecked = returnmaxVoted(voteTocheck);
                    System.out.println("Detectives have picked someone to check ");
                    //System.out.println(playerArrayList.get(votedTochecked));
                    //index of the person who they want to check
                }
                boolean does_heal_heal = false;
                if(healerArrayList.size()>0){
                    does_heal_heal = true;
                }

                int TobeHealed = 0;
                int[] healerVote = new int[playerArrayList.size()];
                //now time for healer to make a move;
                if(does_heal_heal) {
                    HealerHealPerson(healerVote);
                    TobeHealed = returnmaxVoted(healerVote);//index of the person who healer will heal
                    System.out.println("Healer has chosen someone to heal ");
                    //System.out.println(playerArrayList.get(TobeHealed).toString());
                }

                System.out.println("--End of actions--");

                //time to check and modify person's health and checks

                attackTarget(votedBymafia);
                boolean checkiffound = false;
                Player ref = null;
                if(do_detective_check) {
                    checkiffound = checkPerson(votedTochecked);
                    //if detectives has found the mafia then
                    //storing its object reference for removal in next round;
                    ref = playerArrayList.get(votedTochecked);
                }
                if(does_heal_heal) {
                    healPerson(TobeHealed);
                }
                //remove player with 0HP;
                if(!(getPlayerfromID(votedBymafia).getHp()>0)){
                    Player p = getPlayerfromID(votedBymafia);
                    System.out.println(p.toString()+" has died");
                    playerArrayList.remove(p);
                    playersAlivewithID.remove((Integer)p.getId());
                    //size has decreased now use object references further to avoid errors;
                    removeDeadPlayer(p);
                }
                else{
                    System.out.println("No one died");
                }
                if(1+mafiaArrayList.size()==detectives.size()+healerArrayList.size()+commonerArrayList.size()){
                    System.out.println("Mafia have won the game");
                    printPlayerDetails(clonedPlayerlist);
                    break;
                }
                if(checkiffound){
                    System.out.println("No voting this round: ");
                    System.out.println(ref.toString()+" was caught by detectives and has been removed ");
                    playerArrayList.remove(ref);
                    removeDeadPlayer(ref);
                    playersAlivewithID.remove((Integer)ref.getId());
                    if(ref.equals(user)) {
                        System.out.println("Game Over Detective has caught you!");
                        printPlayerDetails(clonedPlayerlist);
                        flag = false;
                    }

                }

                else{
                    int[] votePlayer = new int[playerArrayList.size()];
                    botVotePlayers(votePlayer);
                    System.out.println("Select a player to vote: ");
                    boolean userVote = true;
                    while (userVote){
                        int num = in.nextInt();
                        if(playersAlivewithID.contains(num)){
                            Player u = getPlayerfromID(num);
                            int n = getIndexOfPlayer(u);
                            votePlayer[n]++;
                            userVote = false;
                        }
                        else{
                            System.out.println("please vote a valid player");
                        }
                    }
                    int tobeKicked = returnmaxVoted(votePlayer);
                    Player who_is_kicked = playerArrayList.get(tobeKicked);
                    System.out.println(who_is_kicked.toString()+" has been voted out. ");
                    if(who_is_kicked.equals(user)){
                        System.out.println("Sorry game over");
                        printPlayerDetails(clonedPlayerlist);
                        flag = false;
                    }
                    else {
                        playersAlivewithID.remove((Integer)who_is_kicked.getId());
                        playerArrayList.remove(who_is_kicked);
                        removeDeadPlayer(who_is_kicked);
                        if(1+mafiaArrayList.size()>=detectives.size()+healerArrayList.size()+commonerArrayList.size()) {
                            System.out.println("Mafia have won!");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                    }

                }

            }

        }
        if(user instanceof Detective){
            boolean flag = true;
            System.out.print("You are a detective, other teammates are: ");
            for(Detective detective:detectives){
                System.out.print(detective.toString()+" ");
            }
            System.out.println();
            int round = 1;
            while (flag) {
                if(mafiaArrayList.size()>=detectives.size()+commonerArrayList.size()+healerArrayList.size()+1){
                    System.out.println("Mafia have won the game");
                    printPlayerDetails(clonedPlayerlist);
                    break;
                }
                System.out.println("Round "+round+":");
                round++;

                System.out.print(playerArrayList.size()+" players are remaining: ");
                printPlayer();

                boolean checkMafia = false;
                boolean checkHealers = false;
                if (mafiaArrayList.size() > 0) {
                    checkMafia = true;
                }
                if (healerArrayList.size() > 0) {
                    checkHealers = true;
                }
                int[] mafiaPick = new int[playerArrayList.size()];
                int picked = 0;
                if (checkMafia) {
                    MafiaSelectTarget(mafiaPick);
                    picked = returnmaxVoted(mafiaPick);//returns index of the player who is voted the maximum number of times
                    System.out.println("Mafia's have chosen their target");
                }
                int toBeChecked = 0; //this is basically the id of the person I guess
                boolean voteFlag = true;
                Player p = null;
                System.out.println("Choose a player to test: ");
                //printPlayer();
                while (voteFlag) {
                    int suspect = in.nextInt();
                    if (playersAlivewithID.contains(suspect)) {
                        p = getPlayerfromID(suspect);
                        if (p instanceof Detective) {
                            System.out.println("You cannot test a detective, Choose a player to test: ");
                        } else if (p instanceof Mafia) {
                            System.out.println("Nice work Detective, you have successfully spotted a Mafia " + p.toString());
                            toBeChecked = p.getId();
                            voteFlag = false;
                        } else {
                            System.out.println(p + " was not a Mafia");
                            voteFlag = false;
                        }
                    }
                    else{
                        System.out.println("Please vote a valid player");
                    }
                }
                int[] healerPick = new int[playerArrayList.size()];
                int toBehealed = 0; //index of person to be healed
                if (checkHealers) {
                    HealerHealPerson(healerPick);
                    toBehealed = returnmaxVoted(healerPick);
                    healPerson(toBehealed);
                    System.out.println("Healers has chosen someone to heal");
                }
                System.out.println("--End of actions--");
                if (checkMafia) {
                    Player whoisgettingattacked = playerArrayList.get(picked);
                    attackTarget(whoisgettingattacked.getId());
                    if (!(playerArrayList.get(picked).getHp() > 0)) {
                        Player die = playerArrayList.get(picked);
                        if(die.equals(user)){
                            System.out.println("You have been killed by the mafia's");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                        else {
                            playerArrayList.remove(die);
                            playersAlivewithID.remove((Integer) die.getId());
                            removeDeadPlayer(die);
                            System.out.println(die + " has died.");
                            if(mafiaArrayList.size()>=detectives.size()+commonerArrayList.size()+healerArrayList.size()+1){
                                System.out.println("Mafia's have won the game");
                                printPlayerDetails(clonedPlayerlist);
                                break;
                            }
                        }
                    }
                    else{
                        System.out.println("No one died.");
                    }
                }
                if (p instanceof Mafia) {
                    System.out.println("No voting today, mafia is removed");
                    playerArrayList.remove(p);
                    playersAlivewithID.remove((Integer) p.getId());
                    removeDeadPlayer(p);
                    if(mafiaArrayList.size()==0){
                        System.out.println("Congo, villagers(that means your team) has won the game");
                        printPlayerDetails(clonedPlayerlist);
                        break;
                    }
                } else {
                    System.out.println("Select a player to vote: ");
                    int[] votelist = new int[playerArrayList.size()];
                    botVotePlayers(votelist);
                    boolean votePerson = true;
                    while (votePerson){
                        int num = in.nextInt();
                        if(playersAlivewithID.contains(num)){
                            Player that = getPlayerfromID(num);
                            int vote = getIndexOfPlayer(that);
                            votelist[vote]++;
                            votePerson = false;
                        }
                        else{
                            System.out.println("Please vote a valid player");
                        }


                    }
                    int remo = returnmaxVoted(votelist);
                    Player tobeRemoved = playerArrayList.get(remo);
                    if(tobeRemoved.equals(user)){
                        System.out.println("You have been voted by the village, GAME OVER");
                        break;
                    }
                    else{
                        System.out.println(tobeRemoved.toString()+" has been voted out.");
                        playerArrayList.remove(tobeRemoved);
                        playersAlivewithID.remove((Integer)tobeRemoved.getId());
                        removeDeadPlayer(tobeRemoved);
                        if(mafiaArrayList.size()>=healerArrayList.size()+detectives.size()+commonerArrayList.size()){
                            System.out.println("Mafia's have won the game");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }

                        if(mafiaArrayList.size()==0){
                            System.out.println("All mafia's are eliminated");
                            System.out.println("Villagers have won the game");
                            break;
                        }
                    }


                }
            }
        }
        if(user instanceof Healer) {
            boolean flag = true;
            System.out.println("Hello healer, these are your teammates: ");
            for(Healer h: healerArrayList){
                System.out.print(h.toString()+" ");
            }
            System.out.println();
            int round = 1;
            while (flag) {
                if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                    System.out.println("Mafia's have won the game");
                    printPlayerDetails(clonedPlayerlist);
                    break;
                }

                System.out.println("Round "+round+":");
                round++;

                System.out.print(playerArrayList.size()+" players are remaining: ");
                printPlayer();
                boolean checkMafiapresent = false;
                if (mafiaArrayList.size() > 0) {
                    checkMafiapresent = true;
                }
                int[] mafiaPick = new int[playerArrayList.size()];
                int picked = 0;
                if (checkMafiapresent) {
                    MafiaSelectTarget(mafiaPick);
                    picked = returnmaxVoted(mafiaPick);//returns index of the player who is voted the maximum number of times
                    System.out.println("Mafia's have picked their target");
                }
                boolean detectivePresent = false;
                if (detectives.size() > 0) {
                    detectivePresent = true;
                }
                int[] detectivePick = new int[playerArrayList.size()];
                int detectPick = 0;//this is basically the index of the person detective has checked
                Player p = null;
                boolean votingnextRound = true;
                if (detectivePresent) {
                    DetectiveChoosePerson(detectivePick);
                    detectPick = returnmaxVoted(detectivePick);
                    p = playerArrayList.get(detectPick);
                    System.out.println("Detective have picked a player");
                    if (p instanceof Mafia) {
                        //System.out.println("No voting this round, as detectives have found a mafia");
                        votingnextRound = false;
                    }
                }
                System.out.println("Choose a player to heal");
                boolean voteHealer = true;
                while (voteHealer) {
                    int num = in.nextInt();
                    if (playersAlivewithID.contains(num)) {
                        Player tobehealed = getPlayerfromID(num);
                        healPerson(getIndexOfPlayer(tobehealed));
                        System.out.println("Healer have successfully healed someone");
                        voteHealer = false;
                    } else {
                        System.out.println("Please vote a valid player");
                    }
                }
                System.out.println("--End of actions--");
                if (checkMafiapresent) {
                    Player whoisgettingattacked = playerArrayList.get(picked);
                    attackTarget(whoisgettingattacked.getId());
                    if (!(playerArrayList.get(picked).getHp() > 0)) {
                        Player die = playerArrayList.get(picked);
                        if (die.equals(user)) {
                            System.out.println("You have been killed by the mafia's");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        } else {
                            playerArrayList.remove(die);
                            playersAlivewithID.remove((Integer) die.getId());
                            removeDeadPlayer(die);
                            System.out.println(die + " has died");
                            if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                                System.out.println("Mafia's have won the game");
                                printPlayerDetails(clonedPlayerlist);
                                break;
                            }
                        }
                    }
                }
                if(!votingnextRound){
                    System.out.println(p.toString()+" has been caught by the detectives, thus no voting today");
                    playerArrayList.remove(p);
                    playersAlivewithID.remove((Integer) p.getId());
                    removeDeadPlayer(p);
                    if(mafiaArrayList.size()==0){
                        System.out.println("Mafia's have lost the game");
                        printPlayerDetails(clonedPlayerlist);
                        break;
                    }
                }
                else{
                    System.out.println("Choose a player to vote: ");
                    int[] votelist = new int[playerArrayList.size()];
                    botVotePlayers(votelist);
                    boolean votePerson = true;
                    while (votePerson){
                        int num = in.nextInt();
                        if(playersAlivewithID.contains(num)){
                            Player that = getPlayerfromID(num);
                            int vote = getIndexOfPlayer(that);
                            votelist[vote]++;
                            votePerson = false;
                        }
                        else{
                            System.out.println("Please vote a valid player");
                        }
                    }
                    int remo = returnmaxVoted(votelist);
                    Player tobeRemoved = playerArrayList.get(remo);
                    if(tobeRemoved.equals(user)){
                        System.out.println("You have been voted by the village, GAME OVER");
                        printPlayerDetails(clonedPlayerlist);
                        break;
                    }
                    else{
                        System.out.println(tobeRemoved.toString()+" has been voted out. ");
                        playerArrayList.remove(tobeRemoved);
                        playersAlivewithID.remove((Integer)tobeRemoved.getId());
                        removeDeadPlayer(tobeRemoved);
                        if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                            System.out.println("Mafia's have won the game");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                        if(mafiaArrayList.size()==0){
                            System.out.println("Villagers have won the game");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                    }

                }
            }
        }
        if(user instanceof Commoner){
            boolean flag = true;
            int round = 1;
            while (flag){
                if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                    System.out.println("Mafia's have won the game");
                    printPlayerDetails(clonedPlayerlist);
                    break;
                }
                System.out.println("Round "+round+":");
                round++;

                System.out.print(playerArrayList.size()+" players are remaining: ");
                printPlayer();

                boolean checkMafiapresent = false;
                if (mafiaArrayList.size() > 0) {
                    checkMafiapresent = true;
                }
                int[] mafiaPick = new int[playerArrayList.size()];
                int picked = 0;
                if (checkMafiapresent) {
                    MafiaSelectTarget(mafiaPick);
                    picked = returnmaxVoted(mafiaPick);//returns index of the player who is voted the maximum number of times
                    System.out.println("Mafia's have picked their target");
                }
                boolean detectivePresent = false;
                if (detectives.size() > 0) {
                    detectivePresent = true;
                }
                int[] detectivePick = new int[playerArrayList.size()];
                int detectPick = 0;//this is basically the index of the person detective has checked
                Player p = null;
                boolean votingnextRound = true;
                if (detectivePresent) {
                    DetectiveChoosePerson(detectivePick);
                    detectPick = returnmaxVoted(detectivePick);
                    p = playerArrayList.get(detectPick);
                    System.out.println("Detective have picked a player");
                    if (p instanceof Mafia) {
                        System.out.println("No voting next round, as detectives have found a mafia");
                        votingnextRound = false;
                    }
                }
                boolean does_heal_heal = false;
                if(healerArrayList.size()>0){
                    does_heal_heal = true;
                }

                int TobeHealed = 0;
                int[] healerVote = new int[playerArrayList.size()];
                //now time for healer to make a move;
                if(does_heal_heal) {
                    HealerHealPerson(healerVote);
                    TobeHealed = returnmaxVoted(healerVote);//index of the person who healer will heal
                    healPerson(TobeHealed);
                    System.out.println("Healer have picked a player to heal");
                }
                System.out.println("--End of actions--");
                if (checkMafiapresent) {
                    Player whoisgettingattacked = playerArrayList.get(picked);
                    attackTarget(whoisgettingattacked.getId());
                    if (!(playerArrayList.get(picked).getHp() > 0)) {
                        Player die = playerArrayList.get(picked);
                        if (die.equals(user)) {
                            System.out.println("You have been killed by the mafia's");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        } else {
                            playerArrayList.remove(die);
                            playersAlivewithID.remove((Integer) die.getId());
                            removeDeadPlayer(die);
                            System.out.println(die + " has died");
                            if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                                System.out.println("Mafia's have won the game");
                                printPlayerDetails(clonedPlayerlist);
                                break;
                            }
                        }
                    }
                }
                if(!votingnextRound){
                    System.out.println(p.toString()+" has been caught by the detectives and thus no voting will be there today");
                    playerArrayList.remove(p);
                    playersAlivewithID.remove((Integer) p.getId());
                    removeDeadPlayer(p);
                    if(mafiaArrayList.size()==0){
                        System.out.println("Mafia's have lost the game");
                        printPlayerDetails(clonedPlayerlist);
                        break;
                    }
                }
                else{
                    System.out.println("Choose a player to vote: ");
                    //printPlayer();
                    int[] votelist = new int[playerArrayList.size()];
                    botVotePlayers(votelist);
                    boolean votePerson = true;
                    while (votePerson){
                        int num = in.nextInt();
                        if(playersAlivewithID.contains(num)){
                            Player that = getPlayerfromID(num);
                            int vote = getIndexOfPlayer(that);
                            votelist[vote]++;
                            votePerson = false;
                        }
                        else{
                            System.out.println("Please vote a valid player");
                        }
                    }
                    int remo = returnmaxVoted(votelist);
                    Player tobeRemoved = playerArrayList.get(remo);
                    if(tobeRemoved.equals(user)){
                        System.out.println("You have been voted by the village, GAME OVER");
                        printPlayerDetails(clonedPlayerlist);
                        break;
                    }
                    else{
                        System.out.println(tobeRemoved.toString()+" has been voted out. ");
                        playerArrayList.remove(tobeRemoved);
                        playersAlivewithID.remove((Integer)tobeRemoved.getId());
                        removeDeadPlayer(tobeRemoved);
                        if(mafiaArrayList.size()>=healerArrayList.size()+commonerArrayList.size()+detectives.size()+1){
                            System.out.println("Mafia's have won the game");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                        if(mafiaArrayList.size()==0){
                            System.out.println("Villagers have won the game");
                            printPlayerDetails(clonedPlayerlist);
                            break;
                        }
                    }
                }

            }
        }

    }

    private void assignCharacters(int n){
        while (playerArrayList.size()<n/5+n/10+n/5){
            Random ran = new Random();
            int generate = ran.nextInt(4);
            switch (generate){
                case 1: {
                    int count = 0;
                    for(int i=0;i<playerArrayList.size();++i){
                        Player o = playerArrayList.get(i);
                        if(o instanceof Detective){
                            count++;
                        }
                    }
                    if (count < n / 5) {
                        playerArrayList.add(new Detective());
                    }  //else pass

                }
                case 2: {
                    int count = 0;
                    for(int i=0;i<playerArrayList.size();++i){
                        Player o = playerArrayList.get(i);
                        if(o instanceof Mafia){
                            count++;
                        }
                    }

                    if (count < n / 5) {
                        playerArrayList.add(new Mafia());
                    }  //else just pass

                }
                case 3:{
                    int count = 0;
                    for(int i=0;i<playerArrayList.size();++i){
                        Player o = playerArrayList.get(i);
                        if(o instanceof Healer){
                            count++;
                        }
                    }
                    if(count == 0){
                        playerArrayList.add(new Healer());
                    }
                    else if (count < n / 10) {
                        playerArrayList.add(new Healer());
                    }  //else just pass

                }


            }
        }
        //add one more cuz we already added the user player, so inorder to have same num of players as asked add 1 here
        while (playerArrayList.size()!=n+1){
            playerArrayList.add(new Commoner());
        }
        ArrayList<Integer> list = new ArrayList<>();
        for(int i=1;i<=n+1;++i){
            list.add(i);
        }
        Collections.shuffle(list);


        playerArrayList.shuffleList();
        for(int i=0;i<playerArrayList.size();++i){
            if(playerArrayList.get(i) instanceof Mafia && playerArrayList.get(i)!=user) mafiaArrayList.add((Mafia) playerArrayList.get(i));
            else if(playerArrayList.get(i) instanceof Detective && playerArrayList.get(i)!=user) detectives.add((Detective) playerArrayList.get(i));
            else if(playerArrayList.get(i) instanceof Healer && playerArrayList.get(i)!=user) healerArrayList.add((Healer)playerArrayList.get(i));
            else if(playerArrayList.get(i) instanceof Commoner && playerArrayList.get(i)!=user)commonerArrayList.add((Commoner)playerArrayList.get(i));

            playerArrayList.get(i).setId(list.get(i));
            clonedPlayerlist.add(playerArrayList.get(i));

        }
        for(int i=0;i<playerArrayList.size();++i){
            playersAlivewithID.add(playerArrayList.get(i).getId());
        }

    }
    public void printPlayer(){
        playerArrayList.print();
    }

    public MyGenericList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

}


abstract class Player implements Cloneable{
    //every player has its own hp which is used in comparison if he's against a mafia
    //also every player has his own voting rights lol
    protected int hp;
    protected int id;



    boolean userCheck = false;



    protected String isUser = "";
    Player(){

    }
    public abstract int votePlayer(int n);
    public int getHp() {
        return hp;
    }
    public int getId() {
        return id;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setUserCheck(boolean userCheck) {
        this.userCheck = userCheck;
    }
    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }
    @Override
    public String toString(){
        return "Player"+id+isUser;
    }
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(getClass() == o.getClass()) {
                Player p = (Player) o;
                return getId()==p.getId();
            }

        }
        return false;
    }
    @Override
    public Player clone(){
        try{
        Player copy = (Player) super.clone();
        return copy;
        }
        catch (CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

}
class Commoner extends Player{
    Commoner(){
        this.hp = 1000;
    }
    @Override
    public int votePlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }

}
class Mafia extends Player implements Comparable<Player>{
    private ArrayList<Mafia> mafias;
    Mafia(){
        this.hp = 2500;
    }
    @Override
    public int votePlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
    @Override
    public int compareTo(Player p){
        return this.getHp()-p.getHp();
    }
    public int killPlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
    //overriding the toString method to identity the player more easily without those hashcodes which get printed in default toString method

}
class Detective extends Player{
    private ArrayList<Detective> detectives;
    Detective(){
        this.hp = 800;
    }
    @Override
    public int votePlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
    public int checkPlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
}
class Healer extends Player{
    private ArrayList<Healer> healers;
    Healer(){
        this.hp = 800;
    }
    @Override
    public int votePlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
    public int healPlayer(int n){
        Random ran = new Random();
        return ran.nextInt(n);
    }
}

public class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Random ran = new Random();
        System.out.println("Welcome to Mafia");
        System.out.println("Enter number of players");
        int n = in.nextInt();
        while (n<6){
            System.out.println("Invalid input please try again, number of players must be equal or greater than 6");
            n = in.nextInt();
        }

        System.out.println("Choose a character");
        System.out.println("1) Mafia");
        System.out.println("2) Detective");
        System.out.println("3) Healer");
        System.out.println("4) Commoner");
        System.out.println("5) Assign randomly");
        int num = in.nextInt();
        Player user;
        switch (num){
            case 1:
            {
                user = new Mafia();
                break;
            }

            case 2:

            {
                user = new Detective();
                break;
            }
            case 3:                 {
                user = new Healer();
                break;
            }
            case 4:                {
                user = new Commoner();
                break;
            }
            case 5:{
                int mynum = ran.nextInt(4);
                if(mynum==1) user = new Mafia();
                else if(mynum == 2) user = new Detective();
                else if(mynum == 3) user = new Healer();
                else user = new Commoner();
                break;
            }
            default:
                throw new IllegalStateException("Please put a legal value bro " + num);
        }
        Game mygame = new Game(user,n);




    }
}


/*
Sample Game with output recorded at 10:27pm : : : : DON'T TRY TO MATCH OUTPUTS: : : : :

Welcome to Mafia
Enter number of players
11
Choose a character
1) Mafia
2) Detective
3) Healer
4) Commoner
5) Assign randomly
3
You are Player5
Hello healer, these are your teammates:

Round 1:
11 players are remaining: Player11 Player1 Player5 Player7 Player9 Player10 Player2 Player6 Player4 Player3 Player8
Mafia's have picked their target
Detective have picked a player
Choose a player to heal
5
Healer have successfully healed someone
--End of actions--
Player3 has died
Choose a player to vote:
3
Please vote a valid player
5
Player4 has been voted out.
Round 2:
9 players are remaining: Player11 Player1 Player5 Player7 Player9 Player10 Player2 Player6 Player8
Mafia's have picked their target
Detective have picked a player
Choose a player to heal
2
Healer have successfully healed someone
--End of actions--
Player8 has died
Choose a player to vote:
7
Player10 has been voted out.
Round 3:
7 players are remaining: Player11 Player1 Player5 Player7 Player9 Player2 Player6
Mafia's have picked their target
Detective have picked a player
Choose a player to heal
8
Please vote a valid player
5
Healer have successfully healed someone
--End of actions--
Player7 has died
Choose a player to vote:
7
Please vote a valid player
4
Please vote a valid player
3
Please vote a valid player
2
Player1 has been voted out.
Round 4:
5 players are remaining: Player11 Player5 Player9 Player2 Player6
Mafia's have picked their target
Detective have picked a player
Choose a player to heal
2
Healer have successfully healed someone
--End of actions--
Player11 has died
Choose a player to vote:
2
Player6 has been voted out.
Round 5:
3 players are remaining: Player5 Player9 Player2
Mafia's have picked their target
Detective have picked a player
Choose a player to heal
2
Healer have successfully healed someone
--End of actions--
Choose a player to vote:
2
Player2 has been voted out.
Villagers have won the game
Mafia's are Player2 Player4
Detectives are Player11 Player9
Healers are Player5[USER]
Commoners are Player1 Player7 Player10 Player6 Player3 Player8

Process finished with exit code 0

 */