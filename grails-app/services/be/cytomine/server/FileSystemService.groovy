package be.cytomine.server

import be.cytomine.exception.DeploymentException

/*
 * Copyright (c) 2009-2018. Authors: see NOTICE file.
 *
 * Licensed under the GNU Lesser General Public License, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-2.1.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import utils.ProcUtils

class FileSystemService {

    def makeLocalDirectory(String path) {
        int value = ProcUtils.executeOnShell("mkdir -p " + path)
        ProcUtils.executeOnShell("chmod -R 777 " + path)
        return value
    }

    def move(String source, String destination) {
        return move(new File(source), new File(destination))
    }

    def move(File source, File destination) {
        if (!new File(destination.parent).exists()) {
            makeLocalDirectory(destination.parent)
        }

        int exit = ProcUtils.executeOnShell("""mv "$source.absolutePath" "$destination.absolutePath" """)
        if (exit != 0 || !destination.exists()) {
            log.error destination.absolutePath + " created = " + destination.exists()
            throw new FileNotFoundException(destination.absolutePath + " is not created !")
        }
    }


}
