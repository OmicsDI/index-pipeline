package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.crossreferences.DDIDatasetSimilarityService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIExpDataImportService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.service.similarity.ExpOutputDatasetService;
import uk.ac.ebi.ddi.service.db.service.similarity.TermInDBService;

import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/10/15
 */

public class SimilaryScoreTasklet extends AbstractTasklet{

    List<DataType> typeOfData;

    TermInDBService termInDBService;

    DDIDatasetSimilarityService ddiExpDataProcessService;

    ExpOutputDatasetService expOutputDatasetService;

    DDIExpDataImportService ddiExpDataImportService;

    MongoTemplate mongoTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (DataType type: typeOfData){
            ddiExpDataProcessService.calculateIDFWeight(type.getName());
            ddiExpDataProcessService.calculateSimilarity(type.getName());
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(typeOfData, "the dataType can't be null in the process");
    }

    public List<DataType> getTypeOfData() {
        return typeOfData;
    }

    public void setTypeOfData(List<DataType> typeOfData) {
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
