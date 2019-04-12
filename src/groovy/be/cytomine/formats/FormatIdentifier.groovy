package be.cytomine.formats

import be.cytomine.exception.FormatException

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

import be.cytomine.formats.archive.ArchiveFormat
import be.cytomine.formats.archive.ZipFormat
import be.cytomine.formats.heavyconvertable.CellSensVSIFormat
import be.cytomine.formats.heavyconvertable.DotSlideFormat
import be.cytomine.formats.heavyconvertable.OMETIFFFormat
import be.cytomine.formats.heavyconvertable.ZeissCZIFormat
import be.cytomine.formats.lightconvertable.*
import be.cytomine.formats.lightconvertable.geospatial.GeoJPEG2000Format
import be.cytomine.formats.lightconvertable.geospatial.GeoTIFFFormat
import be.cytomine.formats.lightconvertable.specialtiff.*
import be.cytomine.formats.supported.JPEG2000Format
import be.cytomine.formats.supported.PyramidalTIFFFormat
import be.cytomine.formats.supported.NativeFormat
import be.cytomine.formats.supported.digitalpathology.*
import groovy.util.logging.Log

@Log
class FormatIdentifier {

    private def formats
    private CytomineFile file

    FormatIdentifier(CytomineFile file) {
        initializeFormats()
        setFile(file)
    }

    def setFile(file) {
        this.file = file
        this.formats.each {
            it.file = this.file
        }
    }

    def initializeFormats() {
        this.formats = [
                // Fast detections
                new ZipFormat(),
                new JPEGFormat(),
                new PGMFormat(),
                new PNGFormat(),
                new BMPFormat(),
                new DICOMFormat(),
                new PhotoshopTIFFFormat(),
                new HuronTIFFFormat(),
                new CZITIFFFormat(),
                new OMETIFFFormat(),
                new GeoTIFFFormat(),
                new GeoJPEG2000Format(),

                // Slow detections that must come before others
                new HamamatsuNDPIFormat(),
                new VentanaTIFFFormat(),
                new PhilipsTIFFFormat(),

                // Fast detections that must go last (large detection criteria)
                new JPEG2000Format(), // Accept any JPEG2000
                new PyramidalTIFFFormat(), // Accept any pyramidal TIFF
                new PlanarTIFFFormat(), // Accept any planar TIFF
                new BrokenTIFFFormat(), // Accept any TIFF

                // Slow detections
                new AperioSVSFormat(),
                new HamamatsuVMSFormat(),
                new LeicaSCNFormat(),
                new MiraxMRXSFormat(),
                new SakuraSVSlideFormat(),
                new VentanaBIFFormat(),
                new CellSensVSIFormat(),
                new DotSlideFormat(),
                new ZeissCZIFormat(),
        ]
    }

    def getArchiveFormats() {
        return this.formats.findAll { it instanceof ArchiveFormat }
    }

    def getNativeFormats() {
        return this.formats.findAll { it instanceof NativeFormat }
    }

    def getMultipleFilesFormats() {
        return this.formats.findAll { it instanceof MultipleFilesFormat }
    }

    def getSingleFileFormats() {
        return this.formats.findAll { !(it instanceof MultipleFilesFormat) }
    }

    Format identify() {
        def formatsToTest = (file.isDirectory()) ? getMultipleFilesFormats() : getSingleFileFormats()

        Format detected = formatsToTest.find {
            it.detect()
        }

        if (!detected)
            throw new FormatException("Format not found.")

        log.info("Detected format for $file is $detected")
        return detected
    }

    Format identify(String mimeType, def onlyNative = true) {
        def formatsToTest = (onlyNative) ? getNativeFormats() : this.formats

        Format detected = formatsToTest.find {
            it.mimeType == mimeType
        }

        if (!detected)
            throw new FormatException("Format not found.")

        log.info("Detected format for $file is $detected")
        return detected
    }

    boolean isClassicFolder() {
        if (!file.isDirectory())
            return false

        def detected
        try {
            detected = identify()
        } catch(FormatException ignored) {}

        return detected == null
    }








    static getAvailableArchiveFormats() {
        return [
                new ZipFormat()
        ]
    }

    static getSupportedImageFormats() {
        return [
                new JPEG2000Format(),
                new PyramidalTIFFFormat(),
                new AperioSVSFormat(),
                new HamamatsuNDPIFormat(),
                new HamamatsuVMSFormat(),
                new LeicaSCNFormat(),
                new MiraxMRXSFormat(),
                new SakuraSVSlideFormat(),
                new PhilipsTIFFFormat(),
                new VentanaBIFFormat(),
                new VentanaTIFFFormat()
        ]
    }

    static def getImageFormats(String uploadedFilePath, def imageFormats = [], def parent = null) {
//
//        File uploadedFile = new File(uploadedFilePath)
//
//        if (uploadedFile.isDirectory()) {
//            println "$uploadedFilePath is a directory"
//
//            if (uploadedFile.name == "__MACOSX") return
//            // check if it is a folder containing one multipleFileImage
//            def multipleFileImageFormats = getAvailableHierarchicalMultipleImageFormats() + getAvailableMultipleImageFormats()
//
//            def format = multipleFileImageFormats.find { imageFormat ->
//                imageFormat.file = new CytomineFile(uploadedFilePath)
//                return imageFormat.detect()
//            }
//
//            if (format) {
//                imageFormats << [
//                        absoluteFilePath: format.file.absolutePath,
//                        imageFormat     : format,
//                        parent          : parent
//                ]
//            } else {
//                for (File child : uploadedFile.listFiles()) getImageFormats(child.absolutePath, imageFormats, parent)
//            }
//            return imageFormats
//        }
//
//        def archiveFormats = getAvailableArchiveFormats()
//
//        archiveFormats.each {
//            it.file = new CytomineFile(uploadedFilePath)
//        }
//
//        ArchiveFormat detectedArchiveFormat = archiveFormats.find {
//            it.detect()
//        }
//
//        if (detectedArchiveFormat) { //archive, we need to extract and analyze the content
//
//            String dest = uploadedFile.getParent() + "/" + RandomStringUtils.random(13, (('A'..'Z') + ('0'..'0')).join().toCharArray())
//            detectedArchiveFormat.extract(dest)
//
//            getImageFormats(dest, imageFormats, [absoluteFilePath: uploadedFilePath, imageFormat: detectedArchiveFormat])
//
//        } else {
//            imageFormats << [
//                    uploadedFilePath: uploadedFilePath,
//                    imageFormat     : getImageFormat(new CytomineFile(uploadedFilePath)),
//                    parent          : parent
//            ]
//        }
//        return imageFormats
    }

    static NativeFormat getSupportedImageFormatByMimeType(String fif, String mimeType) {
        def imageFormats = getSupportedImageFormats()

        NativeFormat imageFormat = imageFormats.find {
            it.mimeType == mimeType
        }

        imageFormat.setFile(new CytomineFile(fif))
        return imageFormat

    }

    static Format getImageFormat(def file) {

//        def format
//
//        if (file.isDirectory()) {
//
//            return getMultiFileFormat(file)
//
//        }
//        else {
//
//            Format testedFormat = new ZipFormat()
//            testedFormat.file = file
//            if (testedFormat.detect()) return testedFormat
//
//            testedFormat = getAvailableSingleFileImageFormats().find {
//                println file.class.simpleName
//                it.file = file
//                it.detect()
//            }
////            println "123"
////            testedFormat = new JPEGFormat()
////            println "456"
////            testedFormat.setFile(file)
////            println "789"
//            if (testedFormat.detect()) return testedFormat
//
//            // GeoJP2 tested before classic JP2 as GeoJP2 is a JP2 with some metadata
////            testedFormat = new GeoJPEG2000Format()
////            testedFormat.file = file
////            if (testedFormat.detect()) return testedFormat
////
////            testedFormat = new GeoTIFFFormat()
////            testedFormat.file = file
////            if (testedFormat.detect()) return testedFormat
////
////            testedFormat = new JPEG2000Format()
////            testedFormat.file = file
////            if (testedFormat.detect()) return testedFormat
////
////            testedFormat = new ZeissCZIFormat()
////            testedFormat.file = file
////            if (testedFormat.detect()) return testedFormat
////
////            format = getOpenSlideFormat(filePath)
////            if (format) return format
////
////            format = getTIFFFormat(filePath)
////            if (format) return format
////
////            format = getImageMagickFormat(filePath)
////            if (format) return format
//
//        }
//
//        throw new FormatException("Undetected Format")
    }

}