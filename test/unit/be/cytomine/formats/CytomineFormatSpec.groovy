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

import be.cytomine.client.models.UploadedFile
import be.cytomine.formats.archive.ZipFormat
import be.cytomine.formats.heavyconvertable.CellSensVSIFormat
import be.cytomine.formats.heavyconvertable.ZeissCZIFormat
import be.cytomine.formats.lightconvertable.specialtiff.CZITIFFFormat
import be.cytomine.formats.lightconvertable.specialtiff.PhotoshopTIFFFormat
import be.cytomine.formats.lightconvertable.JPEGFormat
import be.cytomine.formats.lightconvertable.PNGFormat
import be.cytomine.formats.lightconvertable.specialtiff.PlanarTIFFFormat
import be.cytomine.formats.lightconvertable.BMPFormat
import be.cytomine.formats.lightconvertable.PGMFormat
import be.cytomine.formats.lightconvertable.specialtiff.BrokenTIFFFormat
import be.cytomine.formats.lightconvertable.specialtiff.HuronTIFFFormat
import be.cytomine.formats.supported.digitalpathology.*
import be.cytomine.formats.supported.JPEG2000Format
import be.cytomine.formats.supported.PhilipsTIFFFormat
import be.cytomine.formats.supported.PyramidalTIFFFormat
import be.cytomine.formats.supported.VentanaBIFFormat
import be.cytomine.formats.supported.VentanaTIFFFormat

import be.cytomine.image.ImageUtilsController
import grails.test.mixin.TestFor
import grails.util.Holders
import org.apache.commons.io.FileUtils

@TestFor(StorageController)
class CytomineFormatSpec extends Specification {

    final static String IMAGES_REPOSITORY_PATH = "/home/alex/IMS-test"

    private static CytomineFile createCytomineFileFromFilename(def imageFilename, String imageRepository = IMAGES_REPOSITORY_PATH) {
        CytomineFile uploadedFile = new CytomineFile([imageRepository, imageFilename].join(File.separator))
        println uploadedFile
        assert uploadedFile.exists()
        return uploadedFile
    }

    void "test detection PNG 8bits format"() {
        given:
        def uploadedFile = createCytomineFileFromFilename("png-8.png")
        when:
        def format = new FormatIdentifier(uploadedFile).identify()
        then:
        format instanceof PNGFormat
    }

    void "test conversion PNG 8bits format"() {
        given:
        def file = createCytomineFileFromFilename("png-8.png")
        def format = new PNGFormat()
        format.setFile(file)
        format.detect()

        when:
        def converted = format.convert()

        then:
        converted.size() == 1
        new FormatIdentifier(converted.get(0)).identify() instanceof PyramidalTIFFFormat

        cleanup:
        converted.each { it.delete() }
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

    void "test conversion HuronTIFF format"() {
        given:
        def file = createCytomineFileFromFilename("huron.tif")
        def format = new HuronTIFFFormat()
        format.setFile(file)
        format.detect()

        when:
        def converted = format.convert()

        then:
        converted.size() == 1
        new FormatIdentifier(converted.get(0)).identify() instanceof PyramidalTIFFFormat

        cleanup:
        converted.each { it.delete() }
    }
}