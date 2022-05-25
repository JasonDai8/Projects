package gitlet;

import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Logan Dickey, Jason Dai
 */
public class Main {

    private static final String REPO_NAME = "REPOSITORY";

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (!Repository.init(REPO_NAME)) {
                    System.out.println("Gitlet version-control system already exists in the current directory.");
                }
                break;
            case "add":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.add(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "rm":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    repo.remove(args[1]);
                }
                break;
            case "commit":
                if (args.length == 2 && !args[1].equals("")) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.commit(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                break;
            case "dump":
                if (args.length == 1) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        repo.dump();
                    } else {
                        System.out.println("error dumping. invalid repo name");
                    }
                }
                break;
            case "log":
                if (args.length == 1) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.log();
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "find":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.find(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "global-log":
                if (args.length == 1) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.globalLog();
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "status":
                if (args.length == 1) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.status();
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "branch":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.createBranch(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "reset":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.reset(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "rm-branch":
                if (args.length == 2) {
                    Repository repo = Repository.fromFile(REPO_NAME);
                    if (repo != null) {
                        // We have access to a proper repository instance
                        repo.removeBranch(args[1]);
                    } else {
                        // The repo hasn't been created yet
                        System.out.println("Not in an initialized Gitlet directory.");
                        System.exit(0);
                    }
                }
                break;
            case "checkout":
                Repository repo = Repository.fromFile(REPO_NAME);
                if (repo != null) {
                    // We have access to a proper repository instance
                    if (args.length == 2) {
                        repo.checkoutBranch(args[1]);
                    } else if (args.length == 3 && args[1].equals("--")) {
                        repo.checkoutFile(args[2]);
                    } else if (args.length == 4 && args[2].equals("--")) {
                        repo.checkout(args[3], args[1]);
                    } else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                } else {
                    // The repo hasn't been created yet
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                break;
            case "merge":
                repo = Repository.fromFile(REPO_NAME);
                if (repo != null) {
                   if (args.length == 2) {
                       repo.merge(args[1]);
                   } else {
                       System.out.println("Incorrect operands.");
                       System.exit(0);
                   }
                } else {
                    // The repo hasn't been created yet
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
        }

    }
}
