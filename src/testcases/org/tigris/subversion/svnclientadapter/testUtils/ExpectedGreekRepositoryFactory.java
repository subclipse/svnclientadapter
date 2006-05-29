package org.tigris.subversion.svnclientadapter.testUtils;

public class ExpectedGreekRepositoryFactory {

    public static ExpectedWC getGreekWC() {
        ExpectedWC greekWC = new ExpectedWC();
        greekWC.addItem("", null);
        greekWC.addItem("iota", "This is the file 'iota'.");
        greekWC.addItem("A", null);
        greekWC.addItem("A/mu", "This is the file 'mu'.");
        greekWC.addItem("A/B", null);
        greekWC.addItem("A/B/lambda", "This is the file 'lambda'.");
        greekWC.addItem("A/B/E", null);
        greekWC.addItem("A/B/E/alpha", "This is the file 'alpha'.");
        greekWC.addItem("A/B/E/beta", "This is the file 'beta'.");
        greekWC.addItem("A/B/F", null);
        greekWC.addItem("A/C", null);
        greekWC.addItem("A/D", null);
        greekWC.addItem("A/D/gamma", "This is the file 'gamma'.");
        greekWC.addItem("A/D/H", null);
        greekWC.addItem("A/D/H/chi", "This is the file 'chi'.");
        greekWC.addItem("A/D/H/psi", "This is the file 'psi'.");
        greekWC.addItem("A/D/H/omega", "This is the file 'omega'.");
        greekWC.addItem("A/D/G", null);
        greekWC.addItem("A/D/G/pi", "This is the file 'pi'.");
        greekWC.addItem("A/D/G/rho", "This is the file 'rho'.");
        greekWC.addItem("A/D/G/tau", "This is the file 'tau'.");
        return greekWC;
    }

    public static ExpectedRepository getGreekRepository() {
        ExpectedRepository greekRepository = new ExpectedRepository();
        greekRepository.addItem("", null);
        greekRepository.addItem("iota", "This is the file 'iota'.");
        greekRepository.addItem("A", null);
        greekRepository.addItem("A/mu", "This is the file 'mu'.");
        greekRepository.addItem("A/B", null);
        greekRepository.addItem("A/B/lambda", "This is the file 'lambda'.");
        greekRepository.addItem("A/B/E", null);
        greekRepository.addItem("A/B/E/alpha", "This is the file 'alpha'.");
        greekRepository.addItem("A/B/E/beta", "This is the file 'beta'.");
        greekRepository.addItem("A/B/F", null);
        greekRepository.addItem("A/C", null);
        greekRepository.addItem("A/D", null);
        greekRepository.addItem("A/D/gamma", "This is the file 'gamma'.");
        greekRepository.addItem("A/D/H", null);
        greekRepository.addItem("A/D/H/chi", "This is the file 'chi'.");
        greekRepository.addItem("A/D/H/psi", "This is the file 'psi'.");
        greekRepository.addItem("A/D/H/omega", "This is the file 'omega'.");
        greekRepository.addItem("A/D/G", null);
        greekRepository.addItem("A/D/G/pi", "This is the file 'pi'.");
        greekRepository.addItem("A/D/G/rho", "This is the file 'rho'.");
        greekRepository.addItem("A/D/G/tau", "This is the file 'tau'.");

        return greekRepository;
    }

}