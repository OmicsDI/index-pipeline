package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Setter
public class GeoSuperSeriesEnrichmentTasklet extends AbstractTasklet {

    private IDatasetService datasetService;

    private static final String DATASET_NAME = "GEO";

    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";

    @Value("${ddi.common.storage.path}")
    private String downloadPath;

    private static final String TASK_WORKING_DIR = "geo-raw-dataset";

    private File downloadDir;

    private static final int RETRIES = 5;
    private RetryTemplate template = new RetryTemplate();

    private Pattern superSerial = Pattern.compile("!Series_relation\\s*=\\s*SuperSeries of:\\s*([^\\s]*)");

    private static final int PARALLEL = Math.min(9, Runtime.getRuntime().availableProcessors());

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoSuperSeriesEnrichmentTasklet.class);

    public GeoSuperSeriesEnrichmentTasklet() {
        SimpleRetryPolicy policy =
                new SimpleRetryPolicy(RETRIES, Collections.singletonMap(Exception.class, true));
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(1.6);
        template.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Starting");
        downloadDir = new File(downloadPath, TASK_WORKING_DIR);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        List<Dataset> datasets = datasetService.readDatasetHashCode(DATASET_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
        customThreadPool.submit(() -> datasets.stream().parallel().forEach(dataset -> {
            LOGGER.info("Processing dataset " + dataset.getAccession() + ", {}/{}", counter.getAndIncrement(), datasets.size());
            process(dataset);
        })).get();
        LOGGER.info("Finished");
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset dataset) {
        try {
            List<String> superSerialAccessions = getAllSuperSerialAccessions(dataset.getAccession());
            if (superSerialAccessions.size() == 0) {
                return;
            }
            LOGGER.info("Found subseries of dataset {}: {}", dataset.getAccession(), superSerialAccessions);
            superSerialAccessions.stream()
                    .map(x -> String.format("%s~%s", x, getLink(x)))
                    .forEach(x -> DatasetUtils.addAdditionalField(dataset, "additional_accession", x));
            datasetService.save(dataset);
        } catch (IOException e) {
            LOGGER.error("Exception occurred when processing dataset {}", dataset.getAccession(), e);
        }
    }

    private String getLink(String accessionId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accessionId);
        return builder.build().toUriString();
    }

    private List<String> getAllSuperSerialAccessions(String accessionId) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(getDatasetFile(accessionId)))) {
            List<String> superSerialDatasets = new ArrayList<>();
            for(String line; (line = br.readLine()) != null; ) {
                Matcher matcher = superSerial.matcher(line);
                if (matcher.find()) {
                    superSerialDatasets.add(matcher.group(1));
                }
            }
            return superSerialDatasets;
        }
    }

    private File getDatasetFile(String accessionId) throws IOException {
        File downloadedFile = new File(downloadDir, accessionId);
        if (downloadedFile.exists()) {
            return downloadedFile;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accessionId)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        return template.execute(context -> {
            FileUtils.copyURLToFile(builder.build().toUri().toURL(), downloadedFile);
            return downloadedFile;
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
