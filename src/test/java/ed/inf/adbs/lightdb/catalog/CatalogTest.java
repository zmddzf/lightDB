package ed.inf.adbs.lightdb.catalog;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CatalogTest {
	
	@Test
	public void testCatalog() {
		Catalog catalog = Catalog.getInstance("./samples/db");
		Catalog catalog1 = Catalog.getInstance("./samples/db");
		assertTrue(catalog.equals(catalog1));
		
		for(Object tableName: catalog.tables.keySet()) {
			Table table = (Table) catalog.tables.get(tableName);
			System.out.println(table.getTablePath());
			System.out.println(table.getColumns());
		}
		
	}

}
