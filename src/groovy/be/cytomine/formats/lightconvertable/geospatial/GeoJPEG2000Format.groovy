package be.cytomine.formats.lightconvertable.geospatial

import be.cytomine.exception.ConversionException
import be.cytomine.formats.CytomineFile
import be.cytomine.formats.NotNativeFormat
import be.cytomine.formats.detectors.GdalDetector
import grails.util.Holders
import utils.FilesUtils
import utils.MimeTypeUtils
import utils.ProcUtils

class GeoJPEG2000Format extends NotNativeFormat implements GdalDetector {
    GeoJPEG2000Format() {
        extensions = ["jp2"]
        mimeType = MimeTypeUtils.MIMETYPE_JP2
    }

    boolean detect() {
        return this.file.extension() in extensions && GdalDetector.super.detect()
    }

    @Override
    def convert() {
        String targetName = (this.file.name - ".${this.file.extension()}") + "_geo.tif"
        CytomineFile target = new CytomineFile(this.file.parent, FilesUtils.correctFilename(targetName), this.file.c, this.file.z, this.file.t)

        def gdalinfo = this.file.getGdalInfoOutput()
        def nbits = (gdalinfo.contains("Int16")) ? 16 : ((gdalinfo.contains("Int32") || gdalinfo.contains("Float32")) ? 32 : 8)

        def gdal = Holders.config.cytomine.ims.conversion.gdal.executable
        def convertCommand = """$gdal -co "NBITS=$nbits" -co "JPEG_QUALITY=100" -co "WEBP_LEVEL=100" $file.absolutePath $target.absolutePath """
        if (ProcUtils.executeOnShell(convertCommand).exit != 0 || !target.exists()) {
            throw new ConversionException("${file.absolutePath} hasn't been converted to ${target.absolutePath}")
        }

        return [target]
    }
}
