package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class SimilaryScoreTasklet extends AbstractTasklet {

    List<DataType> typeOfData;

    TermInDBService termInDBService;

    DDIDatasetSimilarityService ddiExpDataProcessService;

    ExpOutputDatasetService expOutputDatasetService;

    DDIExpDataImportService ddiExpDataImportService;

    MongoTemplate mongoTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (DataType type: typeOfData) {
            ddiExpDataProcessService.calculateIDFWeight(type.getName());
            ddiExpDataProcessService.calculateSimilarity(type.getName());
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(typeOfData, "the dataType can't be null in the process");
    }
}
