package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.utils.TestUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeoEnrichmentTaskletTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeoEnrichmentTasklet enrichmentTasklet = new GeoEnrichmentTasklet();

    @Test
    public void test_GetSampleIds_MustReturn_142_Samples() throws IOException {
        String res = TestUtils.getResource(this.getClass(), "GSE9309.txt");
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(res, HttpStatus.OK));
        Dataset dataset = new Dataset("GSE9309", "GEO");
        List<String> samples = enrichmentTasklet.getSampleIds(dataset);
        Assert.assertEquals(samples.size(), 142);
        Assert.assertEquals(samples.get(0), "GSM237137");
        Assert.assertEquals(samples.get(141), "GSM237278");
    }

    @Test
    public void test_GetReanalysisDataset_MustReturn_3_Reanalytics() throws IOException {
        String res = TestUtils.getResource(this.getClass(), "GSM237137.txt");
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(res, HttpStatus.OK));
        Set<PublicationDataset> samples = enrichmentTasklet.getReanalysisDataset("GSM237137");
        Assert.assertEquals(samples.size(), 8);
    }
}