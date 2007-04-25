/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.testUtils;

public class ExpectedGreekRepositoryFactory {

    public static ExpectedWC getGreekWC() {
        ExpectedWC greekWC = new ExpectedWC();
        putGreekStructureInto(greekWC);
        return greekWC;
    }

    public static ExpectedRepository getGreekRepository() {
        ExpectedRepository greekRepository = new ExpectedRepository();
        putGreekStructureInto(greekRepository);
        return greekRepository;
    }

    public static ExpectedRepository getNumericRepository() {
        ExpectedRepository numericRepository = new ExpectedRepository();
        putNumericStructureInto(numericRepository);
        return numericRepository;
    }

    public static ExpectedWC getNumericWC() {
        ExpectedWC numericWC = new ExpectedWC();
        putNumericStructureInto(numericWC);
        return numericWC;
    }

    private static void putGreekStructureInto(ExpectedStructure greek) {
        greek.addItem("", null);
        greek.addItem("iota", "This is the file 'iota'.");
        greek.addItem("A", null);
        greek.addItem("A/mu", "This is the file 'mu'.");
        greek.addItem("A/B", null);
        greek.addItem("A/B/lambda", "This is the file 'lambda'.");
        greek.addItem("A/B/E", null);
        greek.addItem("A/B/E/alpha", "This is the file 'alpha'.");
        greek.addItem("A/B/E/beta", "This is the file 'beta'.");
        greek.addItem("A/B/F", null);
        greek.addItem("A/C", null);
        greek.addItem("A/D", null);
        greek.addItem("A/D/gamma", "This is the file 'gamma'.");
        greek.addItem("A/D/H", null);
        greek.addItem("A/D/H/chi", "This is the file 'chi'.");
        greek.addItem("A/D/H/psi", "This is the file 'psi'.");
        greek.addItem("A/D/H/omega", "This is the file 'omega'.");
        greek.addItem("A/D/G", null);
        greek.addItem("A/D/G/pi", "This is the file 'pi'.");
        greek.addItem("A/D/G/rho", "This is the file 'rho'.");
        greek.addItem("A/D/G/tau", "This is the file 'tau'.");
    }

    private static void putNumericStructureInto(ExpectedStructure greek) {
        greek.addItem("", null);
        greek.addItem("zero", "This is the file 'zero'.");
        greek.addItem("1", null);
        greek.addItem("1/eleven", "This is the file 'eleven'.");
        greek.addItem("1/12", null);
        greek.addItem("1/12/twelve", "This is the file 'twelve'.");
        greek.addItem("1/12/123", null);
        greek.addItem("1/13", null);
        greek.addItem("1/14", null);
        greek.addItem("1/14/fourteen", "This is the file 'fourteen'.");
    }

}