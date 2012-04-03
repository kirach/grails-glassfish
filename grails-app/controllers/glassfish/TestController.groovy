package glassfish

class TestController {

	def index() {
		log.error "PARAM!!!"
		[testParam: "testParam5"]
	}
}
