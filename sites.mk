# ======================================================================
# Targets for building site-specific packages of the VOLANO software
# ======================================================================

# Local shell scripts
siteprep = rm -rf ria/*

# Free trial download
.PHONY: localhost
localhost:
	$(siteprep)
	$(MAKE) keys upgrade product checksum

# Sample site-specific targets
.PHONY: example-com
example-com: export SITENAME = $@
example-com: export CODEBASE = *.example.com
example-com: export DOCBASE  = *.example.com *.example.net *.example.org
example-com:
	$(siteprep)
	$(MAKE) keys upgrade product checksum

# Testing package
.PHONY: status6-ca
status6-ca: export SITENAME = $@
status6-ca: export CODEBASE = *.status6.ca
status6-ca: export DOCBASE  = *.status6.ca
status6-ca:
	$(siteprep)
	$(MAKE) keys upgrade product checksum
