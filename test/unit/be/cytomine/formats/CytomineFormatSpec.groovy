package be.cytomine.formats

/*
 * Copyright (c) 2009-2019. Authors: see NOTICE file.
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

import be.cytomine.formats.archive.ZipFormat
import be.cytomine.formats.heavyconvertable.CellSensVSIFormat
import be.cytomine.formats.heavyconvertable.DotSlideFormat
import be.cytomine.formats.heavyconvertable.OMETIFFFormat
import be.cytomine.formats.heavyconvertable.ZeissCZIFormat
import be.cytomine.formats.heavyconvertable.video.MP4Format
import be.cytomine.formats.lightconvertable.*
import be.cytomine.formats.lightconvertable.geospatial.GeoJPEG2000Format
import be.cytomine.formats.lightconvertable.geospatial.GeoTIFFFormat
import be.cytomine.formats.lightconvertable.specialtiff.*
import be.cytomine.formats.supported.JPEG2000Format
import be.cytomine.formats.supported.PyramidalTIFFFormat
import be.cytomine.formats.supported.digitalpathology.*
import be.cytomine.formats.tools.CytomineFile
import be.cytomine.storage.StorageController
import grails.test.mixin.TestFor
import grails.util.Holders
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Specification
import utils.PropertyUtils

@TestFor(StorageController)
class CytomineFormatSpec extends Specification {

    final static String IMAGES_REPOSITORY_PATH = "/home/alex/IMS-test"

    private static CytomineFile createCytomineFileFromFilename(def imageFilename, String imageRepository = IMAGES_REPOSITORY_PATH) 
    {
        CytomineFile uploadedFile = new CytomineFile([imageRepository, imageFilename].join(File.separator))
        println uploadedFile
        assert uploadedFile.exists()
        return uploadedFile
    }

    void "test detection HuronTIFF format"() 
    {
        given:
        def uploadedFile = createCytomineFileFromFilename("huron.tif")
        when:
        def format = new FormatIdentifier(uploadedFile).identify()
        then:
        format instanceof HuronTIFFFormat
    }
}