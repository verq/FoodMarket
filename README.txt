farmer:
 -zaczyna z zasianym polem (czyli ma 0 nawozu i ziarna, ale ma ju¿ zasiane pole wiêc mo¿e sprzedaæ zbo¿e i kupiæ co potrzebuje)
 - sprzedaje przez ca³y rok
 
 chcemy mieæ tygodniow¹ produkcjê a nie dzienn¹, bo klienci kupuj¹ raz w tygodniu
 
 dla ka¿dego agenta
 	protected EnumMap<Products, Integer> buy; - to co chce kupic i w jakiej ilosci
	protected EnumMap<Products, Integer> have; - to co ma BEZ tego co chce sprzedac (czyli to co ma na wlasny uzytek)
	protected EnumMap<Products, Integer> sell; - to co ma ale chce sprzedac
	
serviceDescription : nazwa sprzedawanego produktu