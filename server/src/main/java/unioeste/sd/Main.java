package unioeste.sd;


public class Main {
    private static void printErrorAndExit() {

        System.exit(-1);
    }
    public static void main(String[] args) {
        boolean useTCP = true;
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("-m")) {
                if( args[1].equalsIgnoreCase("udp")) {
                    useTCP = false;
                } else if (!args[1].equalsIgnoreCase("tcp")){
                    printErrorAndExit();
                }
            }
        } else {
            printErrorAndExit();
        }

        Server.start(useTCP);
    }
}