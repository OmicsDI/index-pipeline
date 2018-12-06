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
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeoEnrichmentTaskletTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeoEnrichmentTasklet enrichmentTasklet = new GeoEnrichmentTasklet();

    @Test
    public void test_GetSampleIds_MustReturn_142_Samples() throws IOException, ClassNotFoundException {
        String res = TestUtils.getResource(this.getClass(), "GSE9309.txt");
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(res, HttpStatus.OK));
        List<String> samples = enrichmentTasklet.getSampleIds("GSE9309");
        Assert.assertEquals(142, samples.size());
        Assert.assertEquals("GSM237137", samples.get(0));
        Assert.assertEquals("GSM237278", samples.get(141));
    }

    @Test
    public void test_GetReanalysisDataset_ReanalysisAsDataset_MustReturn_4_Reanalysis() throws IOException {
        String res = TestUtils.getResource(this.getClass(), "GSM237137.txt");
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(res, HttpStatus.OK));
        Set<PublicationDataset> samples = enrichmentTasklet.getReanalysisDataset("GSM237137");
        Assert.assertEquals(4, samples.size());
    }

    @Test
    public void test_GetReanalysisDataset_ReanalysisAsSample_MustReturn_1_Reanalysis() throws Exception {
        String resGSM629855 = TestUtils.getResource(this.getClass(), "GSM629855.txt");
        when(restTemplate.getForEntity(
                eq("https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=GSM629855&targ=self&form=text"), any()))
                .thenReturn(new ResponseEntity<>(resGSM629855, HttpStatus.OK));
        String resGSM821262 = TestUtils.getResource(this.getClass(), "GSM821262.txt");
        when(restTemplate.getForEntity(
                eq("https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=GSM821262&targ=self&form=text"), any()))
                .thenReturn(new ResponseEntity<>(resGSM821262, HttpStatus.OK));
        Set<PublicationDataset> samples = enrichmentTasklet.getReanalysisDataset("GSM629855");
        Assert.assertEquals(1, samples.size());
    }
}