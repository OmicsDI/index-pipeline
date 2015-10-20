package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.model.DatasetTobeEnriched;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.service.DDIDatasetSimilarityService;
import uk.ac.ebi.ddi.annotation.service.DDIExpDataImportService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.service.similarity.ExpOutputDatasetService;
import uk.ac.ebi.ddi.service.db.service.similarity.TermInDBService;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/10/15
 */

public class SimilaryScoreTasklet extends AbstractTasklet{

    DataType typeOfData;

    TermInDBService termInDBService;

    DDIDatasetSimilarityService ddiExpDataProcessService;

    ExpOutputDatasetService expOutputDatasetService;

    DDIExpDataImportService ddiExpDataImportService;

    MongoTemplate mongoTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        ddiExpDataProcessService.calculateIDFWeight(typeOfData.getName());
        ddiExpDataProcessService.calculateSimilarity(typeOfData.getName());
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(typeOfData, "the dataType can't be null in the process");
    }

    public DataType getTypeOfData() {
        return typeOfData;
    }

    public void setTypeOfData(DataType typeOfData) {
        this.typeOfData = typeOfData;
    }

    public TermInDBService getTermInDBService() {
        return termInDBService;
    }

    public void setTermInDBService(TermInDBService termInDBService) {
        this.termInDBService = termInDBService;
    }

    public DDIDatasetSimilarityService getDdiExpDataProcessService() {
        return ddiExpDataProcessService;
    }

    public void setDdiExpDataProcessService(DDIDatasetSimilarityService ddiExpDataProcessService) {
        this.ddiExpDataProcessService = ddiExpDataProcessService;
    }

    public ExpOutputDatasetService getExpOutputDatasetService() {
        return expOutputDatasetService;
    }

    public void setExpOutputDatasetService(ExpOutputDatasetService expOutputDatasetService) {
        this.expOutputDatasetService = expOutputDatasetService;
    }

    public DDIExpDataImportService getDdiExpDataImportService() {
        return ddiExpDataImportService;
    }

    public void setDdiExpDataImportService(DDIExpDataImportService ddiExpDataImportService) {
        this.ddiExpDataImportService = ddiExpDataImportService;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
